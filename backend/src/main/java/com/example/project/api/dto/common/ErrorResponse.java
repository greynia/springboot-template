package com.example.project.api.dto.common;

import java.time.OffsetDateTime;
import java.util.List;

public record ErrorResponse(
        String errorCode,
        String message,
        List<String> details,
        OffsetDateTime timestamp
) {
}
