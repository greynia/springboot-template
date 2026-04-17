package com.example.project.api.dto.auth;

import com.example.project.api.dto.common.UserSummaryResponse;

public record LoginResponse(
        String accessToken,
        UserSummaryResponse user
) {
}
