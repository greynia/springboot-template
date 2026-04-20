package com.example.project.application.supplier;

import com.example.project.api.dto.common.PageResponse;
import com.example.project.api.dto.supplier.SupplierResponse;
import com.example.project.application.exception.ResourceNotFoundApplicationException;
import com.example.project.infrastructure.persistence.jpa.entity.SupplierEntity;
import com.example.project.infrastructure.persistence.jpa.repository.SupplierRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierQueryService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public SupplierQueryService(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<SupplierResponse> listSuppliers(SupplierListFilter filter) {
        int page = Math.max(filter.page(), 1);
        int pageSize = Math.max(filter.pageSize(), 1);
        var pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        var result = supplierRepository.search(normalizeKeyword(filter.keyword()), filter.status(), pageable);

        return PageResponse.from(
                result.getContent().stream().map(supplierMapper::toResponse).toList(),
                page,
                result.getTotalElements(),
                pageSize
        );
    }

    @Transactional(readOnly = true)
    public SupplierResponse getSupplier(Long supplierId) {
        SupplierEntity entity = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundApplicationException("SUPPLIER_NOT_FOUND", "Supplier was not found."));
        return supplierMapper.toResponse(entity);
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String trimmed = keyword.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
