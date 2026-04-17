package com.example.project.api.dto.common;

import java.util.List;

public record PageResponse<T>(
        List<T> items,
        long total
) {
}
