package com.ekapasha.auth_service.role.presentation.dto.role;

import com.ekapasha.auth_service.role.application.query.role.ListRolesQuery;
import com.ekapasha.shared.pagination.DomainPageRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ListRolesFilterDto(
    @Parameter(description = "Workspace ID (optional - if not provided, returns global roles)")
        @Schema(description = "Workspace ID (optional)")
        UUID workspaceId,
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
    @Parameter(description = "Include deleted records", required = false)
        @Schema(description = "Include deleted records", defaultValue = "false")
        Boolean includeDeleted,
    @Parameter(description = "Include global roles when workspaceId is provided", required = false)
        @Schema(description = "Include global roles", defaultValue = "false")
        Boolean includeGlobal,
    @Parameter(description = "Search term", required = false)
        @Schema(description = "Search term", defaultValue = "")
        String search) {
  public boolean includeDeletedOrNot() {
    return includeDeleted != null && includeDeleted;
  }

  public boolean includeGlobalOrNot() {
    return includeGlobal != null && includeGlobal;
  }

  public ListRolesQuery toListRolesQuery() {
    return new ListRolesQuery(
        this.workspaceId,
        this.includeGlobalOrNot(),
        this.search,
        new DomainPageRequest(this.page, this.size),
        this.includeDeletedOrNot());
  }
}
