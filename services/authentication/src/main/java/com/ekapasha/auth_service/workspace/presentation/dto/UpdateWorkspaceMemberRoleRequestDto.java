package com.ekapasha.auth_service.workspace.presentation.dto;

import com.ekapasha.auth_service.workspace.application.command.workspacemember.UpdateWorkspaceMemberRoleCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateWorkspaceMemberRoleRequestDto(
    @Schema(description = "New role UUID to assign to the member", example = "d3b07384-d113-49cd-a5d6-8ee59151b689")
    @NotNull(message = "Role ID must not be null") UUID roleId) {

  public UpdateWorkspaceMemberRoleCommand toCommand(UUID workspaceId, UUID userId, UUID invokedBy) {
    return new UpdateWorkspaceMemberRoleCommand(workspaceId, userId, roleId, invokedBy);
  }
}
