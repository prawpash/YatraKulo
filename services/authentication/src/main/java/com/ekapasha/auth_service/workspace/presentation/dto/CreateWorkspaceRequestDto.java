package com.ekapasha.auth_service.workspace.presentation.dto;

import com.ekapasha.auth_service.workspace.application.command.workspace.CreateWorkspaceCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateWorkspaceRequestDto(
    @Schema(description = "Workspace name", example = "Development Workspace")
    @NotBlank(message = "Name must not be blank")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    String name,

    @Schema(description = "Workspace description", example = "A workspace for general development tasks")
    @Pattern(
        regexp = ".*\\S.*",
        message = "Description must contain at least one non-whitespace character if provided")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    String description,

    @Schema(description = "Whether this workspace should be the default one", example = "true")
    @NotNull(message = "isDefault is required")
    Boolean isDefault
) {
    public CreateWorkspaceCommand toCommand(UUID ownerId, UUID invokedBy) {
        return new CreateWorkspaceCommand(
            this.name,
            this.description,
            ownerId,
            this.isDefault,
            invokedBy
        );
    }
}
