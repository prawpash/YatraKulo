package com.ekapasha.auth_service.workspace.presentation.dto;

import com.ekapasha.auth_service.workspace.application.command.workspace.SetWorkspaceDefaultCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SetDefaultRequestDto(
    @Schema(description = "Whether to set workspace as default", example = "true")
    @NotNull(message = "isDefault is required")
    Boolean isDefault
) {
    public SetWorkspaceDefaultCommand toCommand(UUID id, UUID invokedBy) {
        return new SetWorkspaceDefaultCommand(id, this.isDefault, invokedBy);
    }
}
