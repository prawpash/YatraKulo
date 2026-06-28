package com.ekapasha.auth_service.workspace.presentation.dto;

import com.ekapasha.shared.pagination.DomainPageRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ListWorkspacesFilterDto(
    @NotNull
        @Min(value = 0, message = "Page must be greater than or equal to 0")
        @Parameter(description = "Page number (0-based)", required = true)
        @Schema(description = "Page number (0-based)", defaultValue = "0", example = "0")
        Integer page,
    @NotNull
        @Min(value = 1, message = "Size must be greater than or equal to 1")
        @Parameter(description = "Page size", required = true)
        @Schema(description = "Page size", defaultValue = "10", example = "10")
        Integer size,
    @Parameter(description = "Search by workspace name")
        @Schema(description = "Search by workspace name", example = "Development")
        String search) {
  public DomainPageRequest toPageRequest() {
    return new DomainPageRequest(this.page, this.size);
  }
}
