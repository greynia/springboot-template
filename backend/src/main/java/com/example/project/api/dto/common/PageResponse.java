package com.example.project.api.dto.common;

import java.util.List;

public record PageResponse<T>(
        List<T> items,
        int currentPage,
        long totalCount,
        int pageSize,
        int totalPages
) {

    public static <T> PageResponse<T> from(List<T> items, int currentPage, long totalCount, int pageSize) {
        int normalizedPageSize = Math.max(pageSize, 1);
        int totalPages = totalCount == 0 ? 0 : (int) Math.ceil((double) totalCount / normalizedPageSize);
        return new PageResponse<>(items, currentPage, totalCount, normalizedPageSize, totalPages);
    }
}
