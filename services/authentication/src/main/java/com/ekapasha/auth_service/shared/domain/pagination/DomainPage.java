package com.ekapasha.auth_service.shared.domain.pagination;

import java.util.List;
import java.util.function.Function;

public record DomainPage<T>(
    List<T> content,
    long totalElements,
    int totalPages,
    int currentPage,
    int pageSize
) {
  public <R> DomainPage<R> map(Function<T, R> mapper) {
    return new DomainPage<>(
        content.stream().map(mapper).toList(),
        totalElements,
        totalPages,
        currentPage,
        pageSize
    );
  }
}
