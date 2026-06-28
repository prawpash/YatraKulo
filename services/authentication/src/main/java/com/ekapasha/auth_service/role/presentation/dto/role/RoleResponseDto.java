package com.ekapasha.auth_service.role.presentation.dto.role;

import com.ekapasha.auth_service.role.domain.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record RoleResponseDto(
    @Schema(description = "Role UUID", example = "d3b07384-d113-49cd-a5d6-8ee59151b689")
    UUID id,

    @Schema(description = "Workspace UUID (null for global roles)", example = "a2d1e2e3-bc12-4321-9988-554433221100")
    UUID workspaceId,

    @Schema(description = "Role name", example = "Admin")
    String name,

    @Schema(description = "Role description", example = "Administrator role")
    String description,

    @Schema(description = "Creation timestamp", example = "2026-06-28T10:00:00Z")
    Instant createdAt,

    @Schema(description = "Last update timestamp", example = "2026-06-28T10:30:00Z")
    Instant updatedAt,

    @Schema(description = "Deletion timestamp", example = "2026-06-28T12:00:00Z")
    Instant deletedAt,

    @Schema(description = "Creator user UUID", example = "c4e2a3b0-1122-3344-5566-77889900aabb")
    UUID createdBy,

    @Schema(description = "Last updater user UUID", example = "c4e2a3b0-1122-3344-5566-77889900aabb")
    UUID updatedBy,

    @Schema(description = "Deleter user UUID", example = "c4e2a3b0-1122-3344-5566-77889900aabb")
    UUID deletedBy
) {
  public static RoleResponseDto from(Role role) {
    return new RoleResponseDto(
        role.getId(),
        role.getWorkspaceId().orElse(null),
        role.getName(),
        role.getDescription(),
        role.getCreatedAt(),
        role.getUpdatedAt(),
        role.getDeletedAt().orElse(null),
        role.getCreatedBy().orElse(null),
        role.getUpdatedBy().orElse(null),
        role.getDeletedBy().orElse(null)
    );
  }
}
