package com.ekapasha.auth_service.workspace.presentation.dto;

import com.ekapasha.auth_service.workspace.application.command.workspace.SetWorkspaceDefaultCommand;

import java.util.UUID;

public record SetDefaultRequestDto(
    boolean isDefault
) {
    public SetWorkspaceDefaultCommand toCommand(UUID id, UUID invokedBy) {
        return new SetWorkspaceDefaultCommand(id, this.isDefault, invokedBy);
    }
}
