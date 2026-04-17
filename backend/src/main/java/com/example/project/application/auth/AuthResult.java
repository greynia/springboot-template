package com.example.project.application.auth;

public record AuthResult(
        String accessToken,
        AuthenticatedUser user
) {
}
