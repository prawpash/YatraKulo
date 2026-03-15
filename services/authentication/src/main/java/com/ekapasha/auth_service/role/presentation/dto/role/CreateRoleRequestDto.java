package com.ekapasha.auth_service.role.presentation.dto.role;

import com.ekapasha.auth_service.role.application.command.role.CreateRoleCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateRoleRequestDto(
    @NotNull UUID workspaceId,
    @NotBlank @Size(min = 3, max = 100) String name,
    @Pattern(
            regexp = ".*\\S.*",
            message = "Description must contain at least one non-whitespace character if provided")
        @Size(max = 255)
        String description) {

        public CreateRoleCommand toCreateRoleCommand(UUID invokedBy){
                return new CreateRoleCommand(this.workspaceId, this.name, this.description, invokedBy);
        }
}
