package com.ekapasha.auth_service.role.application.command.role;

import java.util.UUID;

public record UpdateRoleCommand(
    UUID id,
    String name,
    String description,
    UUID invokedBy
) {
}
