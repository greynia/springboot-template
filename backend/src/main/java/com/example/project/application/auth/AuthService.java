package com.example.project.application.auth;

import com.example.project.application.exception.BadRequestApplicationException;
import com.example.project.application.exception.ResourceNotFoundApplicationException;
import com.example.project.infrastructure.persistence.jpa.entity.UserEntity;
import com.example.project.infrastructure.persistence.jpa.repository.UserRepository;
import com.example.project.infrastructure.security.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Transactional(readOnly = true)
    public AuthResult login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestApplicationException("INVALID_CREDENTIALS", "Email or password is incorrect."));

        if (!user.isActive() || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadRequestApplicationException("INVALID_CREDENTIALS", "Email or password is incorrect.");
        }

        AuthenticatedUser authenticatedUser = toAuthenticatedUser(user);
        return new AuthResult(jwtProvider.generateToken(authenticatedUser), authenticatedUser);
    }

    @Transactional(readOnly = true)
    public AuthenticatedUser getProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundApplicationException("USER_NOT_FOUND", "User was not found."));
        return toAuthenticatedUser(user);
    }

    private AuthenticatedUser toAuthenticatedUser(UserEntity user) {
        return new AuthenticatedUser(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }
}
