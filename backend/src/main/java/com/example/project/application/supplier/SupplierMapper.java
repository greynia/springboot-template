package com.example.project.application.supplier;

import com.example.project.api.dto.common.AuditActorResponse;
import com.example.project.api.dto.supplier.SupplierResponse;
import com.example.project.infrastructure.persistence.jpa.entity.SupplierEntity;
import com.example.project.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public SupplierResponse toResponse(SupplierEntity supplier) {
        return new SupplierResponse(
                supplier.getId(),
                supplier.getCode(),
                supplier.getName(),
                supplier.getContactEmail(),
                supplier.getStatus(),
                supplier.getCreatedAt(),
                supplier.getUpdatedAt(),
                toAuditActor(supplier.getCreatedByUser())
        );
    }

    private AuditActorResponse toAuditActor(UserEntity user) {
        if (user == null) return null;
        return new AuditActorResponse(user.getId(), user.getName());
    }
}
