package com.ekapasha.auth_service.workspace.presentation.dto;

import com.ekapasha.auth_service.workspace.application.command.workspacemember.AddWorkspaceMemberCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddWorkspaceMemberRequestDto(
    @Schema(description = "User UUID to add as member", example = "c4e2a3b0-1122-3344-5566-77889900aabb")
    @NotNull(message = "User ID must not be null") UUID userId,

    @Schema(description = "Role UUID to assign to the user in this workspace", example = "d3b07384-d113-49cd-a5d6-8ee59151b689")
    @NotNull(message = "Role ID must not be null") UUID roleId) {

  public AddWorkspaceMemberCommand toCommand(UUID workspaceId, UUID invokedBy) {
    return new AddWorkspaceMemberCommand(workspaceId, userId, roleId, invokedBy);
  }
}
