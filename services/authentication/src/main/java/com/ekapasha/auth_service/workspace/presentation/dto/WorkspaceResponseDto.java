package com.ekapasha.auth_service.workspace.presentation.dto;

import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record WorkspaceResponseDto(
    @Schema(description = "Workspace UUID", example = "a2d1e2e3-bc12-4321-9988-554433221100")
    UUID id,

    @Schema(description = "Workspace name", example = "My Workspace")
    String name,

    @Schema(description = "Workspace description", example = "A workspace for development")
    String description,

    @Schema(description = "Owner user UUID", example = "c4e2a3b0-1122-3344-5566-77889900aabb")
    UUID ownerId,

    @Schema(description = "Whether this is the user's default workspace", example = "true")
    boolean isDefault,

    @Schema(description = "Creation timestamp", example = "2026-06-28T10:00:00Z")
    Instant createdAt,

    @Schema(description = "Last update timestamp", example = "2026-06-28T10:30:00Z")
    Instant updatedAt
) {
  public static WorkspaceResponseDto from(Workspace workspace) {
    return new WorkspaceResponseDto(
        workspace.getId(),
        workspace.getName(),
        workspace.getDescription().orElse(null),
        workspace.getOwnerId(),
        workspace.isDefault(),
        workspace.getCreatedAt(),
        workspace.getUpdatedAt()
    );
  }
}
