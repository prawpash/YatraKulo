package com.ekapasha.auth_service.workspace.presentation.dto;

import com.ekapasha.auth_service.workspace.application.command.workspacemember.UpdateWorkspaceMemberRoleCommand;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateWorkspaceMemberRoleRequestDto(
    @NotNull(message = "Role ID must not be null") UUID roleId) {

  public UpdateWorkspaceMemberRoleCommand toCommand(UUID workspaceId, UUID userId, UUID invokedBy) {
    return new UpdateWorkspaceMemberRoleCommand(workspaceId, userId, roleId, invokedBy);
  }
}
