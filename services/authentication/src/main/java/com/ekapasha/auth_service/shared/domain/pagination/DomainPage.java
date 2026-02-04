package com.ekapasha.auth_service.shared.domain.pagination;

import java.util.List;

public record DomainPage<T>(
    List<T> content,
    long totalElements,
    int totalPages,
    int currentPage,
    int pageSize
) {
}
