package com.ekapasha.auth_service.workspace.presentation.dto;

import com.ekapasha.auth_service.workspace.application.command.workspace.UpdateWorkspaceCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateWorkspaceRequestDto(
    @Schema(description = "New workspace name", example = "Production Workspace")
    @Pattern(
        regexp = ".*\\S.*",
        message = "Name must contain at least one non-whitespace character if provided")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    String name,

    @Schema(description = "New workspace description", example = "Workspace for deployment environments")
    @Pattern(
        regexp = ".*\\S.*",
        message = "Description must contain at least one non-whitespace character if provided")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    String description
) {
    public UpdateWorkspaceCommand toCommand(UUID id, UUID invokedBy) {
        return new UpdateWorkspaceCommand(id, this.name, this.description, invokedBy);
    }
}
