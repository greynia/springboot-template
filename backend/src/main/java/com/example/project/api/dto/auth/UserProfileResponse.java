package com.example.project.api.dto.auth;

import com.example.project.common.enums.UserRole;

public record UserProfileResponse(
        Long id,
        String email,
        String name,
        UserRole role
) {
}
