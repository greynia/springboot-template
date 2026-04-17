package com.example.project.api.dto.common;

import com.example.project.common.enums.UserRole;

public record UserSummaryResponse(
        Long id,
        String email,
        String name,
        UserRole role
) {
}
