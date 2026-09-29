package com.example.forfinal.service;

import com.example.forfinal.dto.AuthResponse;
import com.example.forfinal.dto.LoginRequest;
import com.example.forfinal.dto.RegisterRequest;
import com.example.forfinal.entity.User;
import com.example.forfinal.filter.JwtUtil;
import com.example.forfinal.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 30;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LoginAttemptService loginAttemptService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registration failed: email already registered {}", request.getEmail());
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(sanitizeInput(request.getName()));
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setFailedAttempts(0);
        user.setAccountNonLocked(true);

        userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        log.info("User registered successfully: {}", user.getEmail());
        return new AuthResponse(accessToken, refreshToken, user.getId(), user.getEmail(), user.getName(), user.getRole(), user.getPhone());
    }

    @Transactional
    public AuthResponse login(LoginRequest request, String clientIp) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found {}", request.getEmail());
                    return new RuntimeException("Invalid email or password");
                });

        if (!user.isAccountNonLocked()) {
            if (user.getLockTime() != null &&
                user.getLockTime().plusMinutes(LOCK_DURATION_MINUTES).isBefore(LocalDateTime.now())) {
                user.setAccountNonLocked(true);
                user.setFailedAttempts(0);
                user.setLockTime(null);
                userRepository.save(user);
                log.info("Account auto-unlocked: {}", user.getEmail());
            } else {
                long remainingMinutes = LOCK_DURATION_MINUTES -
                    (user.getLockTime() != null ? java.time.Duration.between(user.getLockTime(), LocalDateTime.now()).toMinutes() : 0);
                log.warn("Login blocked: account locked for {}", user.getEmail());
                throw new RuntimeException("Account locked. Try again in " + Math.max(1, remainingMinutes) + " minute(s)");
            }
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setAccountNonLocked(false);
                user.setLockTime(LocalDateTime.now());
                log.warn("Account locked after {} failed attempts: {}", MAX_FAILED_ATTEMPTS, user.getEmail());
            }
            userRepository.save(user);
            loginAttemptService.registerFailedAttempt(clientIp);
            throw new RuntimeException("Invalid email or password");
        }

        user.setFailedAttempts(0);
        user.setAccountNonLocked(true);
        user.setLockTime(null);
        userRepository.save(user);
        loginAttemptService.registerSuccessfulAttempt(clientIp);

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        log.info("User logged in successfully: {}", user.getEmail());
        return new AuthResponse(accessToken, refreshToken, user.getId(), user.getEmail(), user.getName(), user.getRole(), user.getPhone());
    }

    public AuthResponse refreshAccessToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            log.warn("Refresh token validation failed");
            throw new RuntimeException("Invalid or expired refresh token");
        }

        String email = jwtUtil.extractEmail(refreshToken);
        if (!userRepository.findByEmail(email).isPresent()) {
            log.warn("Refresh token used for non-existent user: {}", email);
            throw new RuntimeException("User not found");
        }

        String newAccessToken = jwtUtil.generateAccessToken(email);
        String newRefreshToken = jwtUtil.generateRefreshToken(email);

        log.info("Tokens refreshed for user: {}", email);
        return new AuthResponse(newAccessToken, newRefreshToken, email, null, null);
    }

    public AuthResponse updateProfile(Long userId, Map<String, Object> updates) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updates.containsKey("name")) {
            user.setName(sanitizeInput((String) updates.get("name")));
        }
        if (updates.containsKey("phone")) {
            user.setPhone((String) updates.get("phone"));
        }
        if (updates.containsKey("currentPassword") && updates.containsKey("newPassword")) {
            String currentPassword = (String) updates.get("currentPassword");
            String newPassword = (String) updates.get("newPassword");
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                log.warn("Profile update failed: incorrect current password for user {}", user.getEmail());
                throw new RuntimeException("Current password is incorrect");
            }
            if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?`~]).{8,}$")) {
                throw new RuntimeException("New password must be at least 8 characters with uppercase, lowercase, number, and special character");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            log.info("Password changed for user: {}", user.getEmail());
        }

        userRepository.save(user);
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        return new AuthResponse(accessToken, refreshToken, user.getId(), user.getEmail(), user.getName(), user.getRole(), user.getPhone());
    }

    private String sanitizeInput(String input) {
        if (input == null) return null;
        return input.replaceAll("<[^>]*>", "").trim();
    }

}
