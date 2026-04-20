package com.example.project.api.controller;

import com.example.project.api.dto.common.PageResponse;
import com.example.project.api.dto.supplier.CreateSupplierRequest;
import com.example.project.api.dto.supplier.SupplierResponse;
import com.example.project.api.dto.supplier.UpdateSupplierRequest;
import com.example.project.application.auth.AuthenticatedUser;
import com.example.project.application.supplier.SupplierApplicationService;
import com.example.project.application.supplier.SupplierListFilter;
import com.example.project.application.supplier.SupplierQueryService;
import com.example.project.common.enums.SupplierStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierApplicationService supplierApplicationService;
    private final SupplierQueryService supplierQueryService;

    public SupplierController(
            SupplierApplicationService supplierApplicationService,
            SupplierQueryService supplierQueryService
    ) {
        this.supplierApplicationService = supplierApplicationService;
        this.supplierQueryService = supplierQueryService;
    }

    @GetMapping
    public PageResponse<SupplierResponse> listSuppliers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) SupplierStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        return supplierQueryService.listSuppliers(new SupplierListFilter(keyword, status, page, pageSize));
    }

    @GetMapping("/{supplierId}")
    public SupplierResponse getSupplier(@PathVariable Long supplierId) {
        return supplierQueryService.getSupplier(supplierId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupplierResponse createSupplier(
            @Valid @RequestBody CreateSupplierRequest request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return supplierApplicationService.createSupplier(request, authenticatedUser);
    }

    @PatchMapping("/{supplierId}")
    public SupplierResponse updateSupplier(
            @PathVariable Long supplierId,
            @Valid @RequestBody UpdateSupplierRequest request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return supplierApplicationService.updateSupplier(supplierId, request, authenticatedUser);
    }

    @DeleteMapping("/{supplierId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSupplier(
            @PathVariable Long supplierId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        supplierApplicationService.deleteSupplier(supplierId, authenticatedUser);
    }
}
