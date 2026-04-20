package com.example.project.api.dto.supplier;

import com.example.project.api.dto.common.AuditActorResponse;
import com.example.project.common.enums.SupplierStatus;
import java.time.OffsetDateTime;

public record SupplierResponse(
        Long id,
        String code,
        String name,
        String contactEmail,
        SupplierStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        AuditActorResponse createdBy
) {
}
