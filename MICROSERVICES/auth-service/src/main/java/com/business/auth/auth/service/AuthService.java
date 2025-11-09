package com.business.auth.auth.service;


import com.business.auth.auth.util.JwtUtil;
import com.business.auth.database.entities.RefreshToken;
import com.business.auth.database.entities.User;
import com.business.auth.database.repositories.RefreshTokenRepository;
import com.business.auth.database.repositories.UserRepository;
import com.business.auth.dataclasses.InlineContent;
import com.business.auth.dataclasses.NotificationRequest;
import com.business.auth.dto.AuthResponse;
import com.business.auth.dto.LoginRequest;
import com.business.auth.dto.RefreshTokenRequest;
import com.business.auth.dto.RegisterRequest;
import com.business.auth.events.EmailNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .roles("USER") // Default role
                .isActive(true)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());

        // Generate tokens
        AuthResponse response = generateAuthResponse(user);

        // Build NotificationRequest
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setTo(user.getEmail());
        notificationRequest.setEmailCode("welcomeEmail");
        notificationRequest.setInlineContents(List.of(
                new InlineContent("businessUserFirstName", "TEXT", user.getFullName())
        ));

        // Fire event after commit (new transaction)
        eventPublisher.publishEvent(new EmailNotificationEvent(this, notificationRequest));

        return response;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Find user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Check if account is active
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is disabled");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        log.info("User logged in successfully: {}", user.getEmail());

        // Generate tokens
        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshTokenValue = request.getRefreshToken();

        // Validate refresh token
        if (!jwtUtil.validateToken(refreshTokenValue)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Check token type
        if (!"REFRESH".equals(jwtUtil.extractTokenType(refreshTokenValue))) {
            throw new RuntimeException("Invalid token type");
        }

        // Find refresh token in database
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        // Check if expired
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        // Get user
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate new access token
        String newAccessToken = jwtUtil.generateAccessToken(
                user.getUserId(),
                user.getEmail(),
                user.getRoles()
        );

        log.info("Access token refreshed for user: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshTokenValue) // Same refresh token
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationTime(newAccessToken) / 1000)
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build();
    }

    @Transactional
    public void logout(String accessToken) {
        // Extract user info
        String userId = jwtUtil.extractUserId(accessToken);
        String jti = jwtUtil.extractJti(accessToken);

        // Delete refresh token from database
        refreshTokenRepository.deleteByUserId(userId);

        // Add access token to blacklist in Redis
        long expirationTime = jwtUtil.getExpirationTime(accessToken);
        if (expirationTime > 0) {
            String blacklistKey = "blacklist:" + jti;
            redisTemplate.opsForValue().set(
                    blacklistKey,
                    userId,
                    expirationTime,
                    TimeUnit.MILLISECONDS
            );
        }

        log.info("User logged out successfully: userId={}", userId);
    }

    // Helper method to generate auth response
    private AuthResponse generateAuthResponse(User user) {
        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(
                user.getUserId(),
                user.getEmail(),
                user.getRoles()
        );

        String refreshTokenValue = jwtUtil.generateRefreshToken(
                user.getUserId(),
                user.getEmail()
        );

        // Save refresh token to database
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getUserId())
                .token(refreshTokenValue)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        // Delete old refresh token if exists
        refreshTokenRepository.findByUserId(user.getUserId())
                .ifPresent(refreshTokenRepository::delete);

        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationTime(accessToken) / 1000)
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build();
    }

    // Check if token is blacklisted
    public boolean isTokenBlacklisted(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + jti));
    }
}
