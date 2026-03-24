package com.ekapasha.auth_service.role.presentation.dto.role;

import com.ekapasha.auth_service.role.application.command.role.UpdateRoleCommand;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateRoleRequestDto(
    @Pattern(
        regexp = ".*\\S.*",
        message = "Description must contain at least one non-whitespace character if provided")
    @Size(max = 100)
    String name,

    @Pattern(
        regexp = ".*\\S.*",
        message = "Description must contain at least one non-whitespace character if provided")
    @Size(max = 255)
    String description
) {
  public UpdateRoleCommand toUpdateRoleCommand(UUID id, UUID invokedBy){
    return new UpdateRoleCommand(id, this.name, this.description, invokedBy);
  }
}
