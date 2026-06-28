package com.ekapasha.auth_service.shared.presentation.dto;

import com.ekapasha.shared.pagination.DomainPage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record PageResponseDto<T>(
    @Schema(description = "List of elements on the current page")
    List<T> content,

    @Schema(description = "Total number of elements across all pages", example = "100")
    long totalElements,

    @Schema(description = "Total number of pages", example = "10")
    int totalPages,

    @Schema(description = "Current page number (0-indexed)", example = "0")
    int currentPage,

    @Schema(description = "Number of items per page", example = "10")
    int pageSize
) {
  public static <T> PageResponseDto<T> from(DomainPage<T> page) {
    return new PageResponseDto<>(
        page.content(),
        page.totalElements(),
        page.totalPages(),
        page.currentPage(),
        page.pageSize()
    );
  }

  public static <T, R> PageResponseDto<R> from(DomainPage<T> page, java.util.function.Function<T, R> mapper) {
    return new PageResponseDto<>(
        page.content().stream().map(mapper).toList(),
        page.totalElements(),
        page.totalPages(),
        page.currentPage(),
        page.pageSize()
    );
  }
}
