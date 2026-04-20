package com.example.project.infrastructure.persistence.jpa.repository;

import com.example.project.common.enums.SupplierStatus;
import com.example.project.infrastructure.persistence.jpa.entity.SupplierEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupplierRepository extends JpaRepository<SupplierEntity, Long> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    @Query("""
            select supplier
            from SupplierEntity supplier
            where (
                :keyword is null
                or lower(supplier.code) like lower(concat('%', :keyword, '%'))
                or lower(supplier.name) like lower(concat('%', :keyword, '%'))
            )
            and (:status is null or supplier.status = :status)
            """)
    Page<SupplierEntity> search(
            @Param("keyword") String keyword,
            @Param("status") SupplierStatus status,
            Pageable pageable
    );
}
