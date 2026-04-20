package com.example.project.application.supplier;

import com.example.project.api.dto.supplier.CreateSupplierRequest;
import com.example.project.api.dto.supplier.SupplierResponse;
import com.example.project.api.dto.supplier.UpdateSupplierRequest;
import com.example.project.application.auth.AuthenticatedUser;
import com.example.project.application.exception.BadRequestApplicationException;
import com.example.project.application.exception.ForbiddenApplicationException;
import com.example.project.application.exception.ResourceNotFoundApplicationException;
import com.example.project.common.enums.SupplierStatus;
import com.example.project.common.enums.UserRole;
import com.example.project.infrastructure.persistence.jpa.entity.SupplierEntity;
import com.example.project.infrastructure.persistence.jpa.repository.SupplierRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierApplicationService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public SupplierApplicationService(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    @Transactional
    public SupplierResponse createSupplier(CreateSupplierRequest request, AuthenticatedUser actor) {
        assertAdmin(actor);
        assertCodeAvailable(request.code(), null);

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        SupplierEntity entity = new SupplierEntity();
        entity.setCode(request.code().trim());
        entity.setName(request.name().trim());
        entity.setContactEmail(request.contactEmail().trim());
        entity.setStatus(SupplierStatus.ACTIVE);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return supplierMapper.toResponse(supplierRepository.save(entity));
    }

    @Transactional
    public SupplierResponse updateSupplier(Long supplierId, UpdateSupplierRequest request, AuthenticatedUser actor) {
        assertAdmin(actor);
        SupplierEntity entity = getSupplierEntity(supplierId);
        assertCodeAvailable(request.code(), supplierId);

        entity.setCode(request.code().trim());
        entity.setName(request.name().trim());
        entity.setContactEmail(request.contactEmail().trim());
        entity.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        return supplierMapper.toResponse(supplierRepository.save(entity));
    }

    @Transactional
    public void deleteSupplier(Long supplierId, AuthenticatedUser actor) {
        assertAdmin(actor);
        SupplierEntity entity = getSupplierEntity(supplierId);
        supplierRepository.delete(entity);
    }

    private void assertAdmin(AuthenticatedUser actor) {
        if (actor.role() != UserRole.ADMIN) {
            throw new ForbiddenApplicationException("SUPPLIER_MANAGE_FORBIDDEN", "Only admins can manage suppliers.");
        }
    }

    private void assertCodeAvailable(String code, Long currentSupplierId) {
        String normalizedCode = code.trim();
        boolean exists = currentSupplierId == null
                ? supplierRepository.existsByCodeIgnoreCase(normalizedCode)
                : supplierRepository.existsByCodeIgnoreCaseAndIdNot(normalizedCode, currentSupplierId);

        if (exists) {
            throw new BadRequestApplicationException("SUPPLIER_CODE_DUPLICATED", "Supplier code already exists.");
        }
    }

    private SupplierEntity getSupplierEntity(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundApplicationException("SUPPLIER_NOT_FOUND", "Supplier was not found."));
    }
}
