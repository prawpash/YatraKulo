package com.ekapasha.auth_service.workspace.presentation.dto;

import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record WorkspaceMemberResponseDto(
    @Schema(description = "Workspace member record UUID", example = "f4c2a3b0-1122-3344-5566-77889900aabb")
    UUID id,

    @Schema(description = "Workspace UUID", example = "a2d1e2e3-bc12-4321-9988-554433221100")
    UUID workspaceId,

    @Schema(description = "User UUID", example = "c4e2a3b0-1122-3344-5566-77889900aabb")
    UUID userId,

    @Schema(description = "Role UUID assigned to member", example = "d3b07384-d113-49cd-a5d6-8ee59151b689")
    UUID roleId,

    @Schema(description = "Timestamp when user was added to workspace", example = "2026-06-28T10:00:00Z")
    Instant addedAt,

    @Schema(description = "Last update timestamp", example = "2026-06-28T10:30:00Z")
    Instant updatedAt,

    @Schema(description = "User UUID who added this member", example = "c4e2a3b0-1122-3344-5566-77889900aabb")
    UUID addedBy,

    @Schema(description = "User UUID who last updated this member", example = "c4e2a3b0-1122-3344-5566-77889900aabb")
    UUID updatedBy
) {
  public static WorkspaceMemberResponseDto from(WorkspaceMember member) {
    return new WorkspaceMemberResponseDto(
        member.getId(),
        member.getWorkspaceId(),
        member.getUserId(),
        member.getRoleId(),
        member.getAddedAt(),
        member.getUpdatedAt(),
        member.getAddedBy().orElse(null),
        member.getUpdatedBy().orElse(null)
    );
  }
}
