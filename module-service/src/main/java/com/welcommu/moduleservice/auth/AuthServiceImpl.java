package com.welcommu.moduleservice.auth;

import com.welcommu.modulecommon.exception.CustomErrorCode;
import com.welcommu.modulecommon.exception.CustomException;
import com.welcommu.modulecommon.token.JwtDto;
import com.welcommu.modulecommon.token.JwtProvider;
import com.welcommu.moduledomain.user.User;
import com.welcommu.moduleservice.auth.dto.LoginRequest;
import com.welcommu.moduleservice.auth.dto.LoginResponse;
import com.welcommu.moduleservice.redis.RefreshTokenService;
import com.welcommu.moduleservice.user.UserService;
import com.welcommu.moduleservice.user.dto.UserResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Override
    public LoginResponse createToken(LoginRequest request) {
        User user = authenticateUser(request.getEmail(), request.getPassword());
        UserResponse userDto = UserResponse.from(user);

        Map<String, Object> accessClaims = createAccessClaims(userDto);
        Map<String, Object> refreshClaims = createRefreshClaims(userDto);

        JwtDto accessToken = jwtProvider.issueAccessToken(accessClaims);
        JwtDto refreshToken = jwtProvider.issueRefreshToken(refreshClaims);

        saveRefreshToken(user.getId(), refreshToken);

        return buildLoginResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public LoginResponse reIssueToken(String refreshTokenHeader) {
        String oldToken = JwtProvider.withoutBearer(refreshTokenHeader);
        log.info("[reIssueToken] incoming raw refreshToken = {}", oldToken);

        Map<String, Object> claims = jwtProvider.validationTokenWithThrow(oldToken);
        validateRefreshTokenType(claims);

        long userId = parseUserId(claims.get("userId"));

        String stored = refreshTokenService.get(userId);
        log.info("[reIssueToken] stored refreshToken in Redis for user {} = {}", userId, stored);

        verifyAndRotateRefreshToken(userId, oldToken);
        log.info("[reIssueToken] Redis verification passed, rotating token");

        claims.put("jti", UUID.randomUUID().toString());
        JwtDto newAccessToken = jwtProvider.issueAccessToken(claims);
        JwtDto newRefreshToken = jwtProvider.issueRefreshToken(claims);

        saveRefreshToken(userId, newRefreshToken);
        log.info("[reIssueToken] new refreshToken saved to Redis = {}", newRefreshToken.getToken());

        return buildLoginResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public void deleteToken(String refreshTokenHeader) {
        String refreshToken = JwtProvider.withoutBearer(refreshTokenHeader);
        Map<String, Object> claims = jwtProvider.validationTokenWithThrow(refreshToken);
        long userId = parseUserId(claims.get("userId"));

        refreshTokenService.delete(userId);
    }

    private User authenticateUser(String email, String password) {
        User user = userService.getUserByEmail(email)
            .orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(CustomErrorCode.INVALID_CREDENTIALS);
        }
        return user;
    }

    private Map<String, Object> createAccessClaims(UserResponse userDto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userDto.getEmail());
        claims.put("userId", userDto.getId());
        claims.put("role", userDto.getCompanyRole());
        return claims;
    }

    private Map<String, Object> createRefreshClaims(UserResponse userDto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userDto.getEmail());
        claims.put("userId", userDto.getId());
        return claims;
    }

    private void saveRefreshToken(Long userId, JwtDto refreshToken) {
        refreshTokenService.save(
            userId,
            refreshToken.getToken(),
            calcExpireSeconds(refreshToken.getExpiredAt())
        );
    }

    private void verifyAndRotateRefreshToken(long userId, String oldToken) {
        if (!refreshTokenService.isValid(userId, oldToken)) {
            refreshTokenService.delete(userId);
            throw new CustomException(CustomErrorCode.INVALID_TOKEN);
        }
        refreshTokenService.delete(userId);
    }

    private void validateRefreshTokenType(Map<String, Object> claims) {
        String tokenType = (String) claims.get("tokenType");
        if (!"refresh".equals(tokenType)) {
            throw new CustomException(CustomErrorCode.INVALID_REFRESH_TOKEN_TYPE);
        }
    }

    private long parseUserId(Object raw) {
        if (raw instanceof Integer) return ((Integer) raw).longValue();
        if (raw instanceof Long)    return (Long) raw;
        if (raw instanceof String)  return Long.parseLong((String) raw);
        throw new CustomException(CustomErrorCode.INVALID_USERID_TYPE);
    }

    private long calcExpireSeconds(LocalDateTime expiresAt) {
        long seconds = Duration.between(LocalDateTime.now(), expiresAt).getSeconds();
        return Math.max(seconds, 1);
    }

    private LoginResponse buildLoginResponse(JwtDto accessToken, JwtDto refreshToken) {
        return LoginResponse.builder()
            .accessToken(JwtProvider.withBearer(accessToken.getToken()))
            .refreshToken(JwtProvider.withBearer(refreshToken.getToken()))
            .build();
    }
}
