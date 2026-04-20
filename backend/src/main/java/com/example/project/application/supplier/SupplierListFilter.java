package com.example.project.application.supplier;

import com.example.project.common.enums.SupplierStatus;

public record SupplierListFilter(
        String keyword,
        SupplierStatus status,
        int page,
        int pageSize
) {
}
