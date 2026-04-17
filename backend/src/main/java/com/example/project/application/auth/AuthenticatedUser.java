package com.example.project.application.auth;

import com.example.project.common.enums.UserRole;

public record AuthenticatedUser(
        Long id,
        String email,
        String name,
        UserRole role
) {
}
