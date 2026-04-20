package com.example.project.api.dto.common;

import java.time.OffsetDateTime;
import java.util.List;

public record ErrorResponse(
        String errorCode,
        String message,
        List<FieldViolation> errors,
        OffsetDateTime timestamp,
        String requestId
) {
    public record FieldViolation(String field, String message) {}
}
