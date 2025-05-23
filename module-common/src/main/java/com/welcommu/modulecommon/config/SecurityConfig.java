package com.welcommu.modulecommon.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.welcommu.modulecommon.filter.JwtAuthenticationFilter;
import com.welcommu.modulecommon.token.JwtProvider;
import jakarta.servlet.DispatcherType;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] SWAGGER = {
        "/swagger-ui/index.html",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    };

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper; 

    @Value("${cors.allowedOrigins}")
    private String allowedOrigins;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, userDetailsService, objectMapper);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtProvider,
            userDetailsService,
            objectMapper
        );

        httpSecurity
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(it -> it
                .requestMatchers(
                    PathRequest.toStaticResources().atCommonLocations()
                ).permitAll()
                .requestMatchers(HttpMethod.GET, SWAGGER).permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/users/resetpassword").permitAll()
                .requestMatchers("/swagger-ui/*").permitAll()
                .requestMatchers("/api/auth/refresh-token").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/request-reset").permitAll()
                .requestMatchers(HttpMethod.PUT,  "/api/users/confirm-reset").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        log.info("🔥 Security 설정 적용됨!");

        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
            Arrays.asList("http://localhost:3000", "https://localhost:3000", "https://www.vivim.co.kr", "https://test.vivim.co.kr"));
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
