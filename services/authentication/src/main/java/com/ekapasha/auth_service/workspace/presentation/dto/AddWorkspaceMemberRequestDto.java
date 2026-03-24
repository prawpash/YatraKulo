package com.ekapasha.auth_service.workspace.presentation.dto;

import com.ekapasha.auth_service.workspace.application.command.workspacemember.AddWorkspaceMemberCommand;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddWorkspaceMemberRequestDto(
    @NotNull(message = "User ID must not be null") UUID userId,
    @NotNull(message = "Role ID must not be null") UUID roleId) {

  public AddWorkspaceMemberCommand toCommand(UUID workspaceId, UUID invokedBy) {
    return new AddWorkspaceMemberCommand(workspaceId, userId, roleId, invokedBy);
  }
}
