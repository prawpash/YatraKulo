package com.ekapasha.auth_service.role.presentation.dto.role;

import com.ekapasha.auth_service.role.application.command.role.CreateRoleCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateRoleRequestDto(
    @Schema(description = "Workspace UUID", example = "a2d1e2e3-bc12-4321-9988-554433221100")
    @NotNull UUID workspaceId,

    @Schema(description = "Role name", example = "Admin")
    @NotBlank @Size(min = 3, max = 100) String name,

    @Schema(description = "Role description", example = "Administrator role with full workspace access")
    @Pattern(
            regexp = ".*\\S.*",
            message = "Description must contain at least one non-whitespace character if provided")
        @Size(max = 255)
        String description) {

        public CreateRoleCommand toCreateRoleCommand(UUID invokedBy){
                return new CreateRoleCommand(this.workspaceId, this.name, this.description, invokedBy);
        }
}
