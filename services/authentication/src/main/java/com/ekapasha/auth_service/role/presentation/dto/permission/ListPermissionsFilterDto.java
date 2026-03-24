package com.ekapasha.auth_service.role.presentation.dto.permission;

import com.ekapasha.auth_service.role.application.query.permission.ListPermissionsQuery;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPageRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ListPermissionsFilterDto(
    @NotNull
        @Min(value = 0, message = "Page must be greater than or equal to 0")
        @Parameter(description = "Page number (0-based)", required = true)
        @Schema(description = "Page number (0-based)", defaultValue = "0")
        Integer page,
    @NotNull
        @Min(value = 1, message = "Size must be greater than or equal to 1")
        @Parameter(description = "Page size", required = true)
        @Schema(description = "Page size", defaultValue = "10")
        Integer size,
    @Parameter(description = "Search term", required = false)
        @Schema(description = "Search term", defaultValue = "")
        String search) {
  public ListPermissionsQuery toListPermissionsQuery() {
    return new ListPermissionsQuery(this.search, new DomainPageRequest(this.page, this.size));
  }
}
