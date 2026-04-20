package com.example.project.application.auth;

import com.example.project.application.exception.BadRequestApplicationException;
import com.example.project.application.exception.ResourceNotFoundApplicationException;
import com.example.project.infrastructure.persistence.jpa.entity.RefreshTokenEntity;
import com.example.project.infrastructure.persistence.jpa.entity.UserEntity;
import com.example.project.infrastructure.persistence.jpa.repository.RefreshTokenRepository;
import com.example.project.infrastructure.persistence.jpa.repository.UserRepository;
import com.example.project.infrastructure.security.JwtProvider;
import com.example.project.infrastructure.security.RefreshTokenManager;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenManager refreshTokenManager;
    private final PasswordPolicyValidator passwordPolicyValidator;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider,
            RefreshTokenManager refreshTokenManager,
            PasswordPolicyValidator passwordPolicyValidator
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.refreshTokenManager = refreshTokenManager;
        this.passwordPolicyValidator = passwordPolicyValidator;
    }

    @Transactional
    public AuthResult login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestApplicationException("INVALID_CREDENTIALS", "Email or password is incorrect."));

        if (!user.isActive() || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadRequestApplicationException("INVALID_CREDENTIALS", "Email or password is incorrect.");
        }

        AuthenticatedUser authenticatedUser = toAuthenticatedUser(user);
        return issueTokens(user, authenticatedUser);
    }

    @Transactional
    public AuthResult refresh(String refreshToken) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        RefreshTokenEntity storedToken = refreshTokenRepository.findByTokenHash(refreshTokenManager.hashToken(refreshToken))
                .orElseThrow(() -> new BadRequestApplicationException("INVALID_REFRESH_TOKEN", "Refresh token is invalid or expired."));

        if (storedToken.getRevokedAt() != null || storedToken.getExpiresAt().isBefore(now)) {
            throw new BadRequestApplicationException("INVALID_REFRESH_TOKEN", "Refresh token is invalid or expired.");
        }

        UserEntity user = storedToken.getUser();
        if (!user.isActive()) {
            throw new BadRequestApplicationException("INVALID_REFRESH_TOKEN", "Refresh token is invalid or expired.");
        }

        refreshTokenRepository.revokeById(storedToken.getId(), now);

        AuthenticatedUser authenticatedUser = toAuthenticatedUser(user);
        return issueTokens(user, authenticatedUser);
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.revokeActiveTokensByUserId(userId, OffsetDateTime.now(ZoneOffset.UTC));
    }

    @Transactional(readOnly = true)
    public AuthenticatedUser getProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundApplicationException("USER_NOT_FOUND", "User was not found."));
        return toAuthenticatedUser(user);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundApplicationException("USER_NOT_FOUND", "User was not found."));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BadRequestApplicationException("INVALID_CREDENTIALS", "Current password is incorrect.");
        }
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new BadRequestApplicationException("PASSWORD_REUSE_NOT_ALLOWED", "New password must be different from the current password.");
        }

        passwordPolicyValidator.validate(newPassword);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        refreshTokenRepository.revokeActiveTokensByUserId(userId, now);
    }

    private AuthResult issueTokens(UserEntity user, AuthenticatedUser authenticatedUser) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        String refreshToken = refreshTokenManager.generateToken();
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setTokenHash(refreshTokenManager.hashToken(refreshToken));
        refreshTokenEntity.setExpiresAt(OffsetDateTime.ofInstant(refreshTokenManager.calculateExpiry(), ZoneOffset.UTC));
        refreshTokenEntity.setCreatedAt(now);
        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResult(jwtProvider.generateToken(authenticatedUser), refreshToken, authenticatedUser);
    }

    private AuthenticatedUser toAuthenticatedUser(UserEntity user) {
        return new AuthenticatedUser(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }
}
