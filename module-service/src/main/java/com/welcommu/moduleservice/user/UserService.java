package com.welcommu.moduleservice.user;

import static com.welcommu.modulecommon.exception.CustomErrorCode.NOT_FOUND_COMPANY;
import static com.welcommu.modulecommon.exception.CustomErrorCode.NOT_FOUND_USER;

import com.welcommu.modulecommon.exception.CustomErrorCode;
import com.welcommu.modulecommon.exception.CustomException;
import com.welcommu.moduledomain.company.Company;
import com.welcommu.moduledomain.user.User;
import com.welcommu.modulerepository.company.CompanyRepository;
import com.welcommu.modulerepository.user.UserRepository;
import com.welcommu.moduleservice.user.audit.UserAuditService;
import com.welcommu.moduleservice.user.dto.UserSnapshot;
import com.welcommu.moduleservice.user.dto.UserModifyRequest;
import com.welcommu.moduleservice.user.dto.UserRequest;
import com.welcommu.moduleservice.user.dto.UserResponse;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final UserAuditService userAuditService;

    @Transactional
    public void createUser(UserRequest request, Long creatorId) {
        Company company = findCompany(request.getCompanyId());

        User user = request.toEntity(company, passwordEncoder);
        User savedUser =  userRepository.saveAndFlush(user);
        userAuditService.createAuditLog(UserSnapshot.from(savedUser), creatorId);
    }

    public UserResponse modifyUser(Long id, Long creatorId, UserModifyRequest request) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_USER));
        Company company = findCompany(request.getCompanyId());
        UserSnapshot beforeSnapshot = UserSnapshot.from(existingUser);
        request.modifyUser(existingUser, company);
        User savedUser = userRepository.save(existingUser);
        UserSnapshot afterSnapshot = UserSnapshot.from(savedUser);

        userAuditService.modifyAuditLog(beforeSnapshot, afterSnapshot,creatorId);
        return UserResponse.from(savedUser);
    }

    private Company findCompany(Long request) {
        return companyRepository.findById(request)
            .orElseThrow(() -> new CustomException(NOT_FOUND_COMPANY));
    }


    public boolean resetPasswordWithoutLogin(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        log.info(String.valueOf(existingUser));
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            log.info("기존 사용자 데이터: " + user);
            String encryptedPassword = passwordEncoder.encode("1q2w3e4r");
            user.setPassword(encryptedPassword);
            userRepository.save(user);
            return true;
        } else {
            log.warn("사용자 존재하지 않음: email =" + email);
            return false;
        }
    }

    public boolean modifyPassword(Long id, String password) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            String encryptedPassword = passwordEncoder.encode(password);
            user.setPassword(encryptedPassword);
            userRepository.save(user);
            return true;
        } else {
            log.warn("사용자 존재하지 않음: id =" + id);
            return false;
        }

    }

    public void deleteUser(Long id,Long actorId ) {
        User user = userRepository.findById(id).orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_USER));
        userAuditService.deleteAuditLog(UserSnapshot.from(user),actorId);
        userRepository.deleteById(id);
    }

    public void softDeleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setIsDeleted(true);
            existingUser.setDeletedAt(java.time.LocalDateTime.now());
            userRepository.save(existingUser);
        } else {
            throw new CustomException(NOT_FOUND_USER);
        }
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
            .map(UserResponse::from)
            .collect(Collectors.toList());
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }
}
