package com.example.project.application.auth;

public record AuthResult(
        String accessToken,
        String refreshToken,
        AuthenticatedUser user
) {
}
