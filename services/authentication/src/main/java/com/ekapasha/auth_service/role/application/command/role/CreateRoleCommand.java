package com.ekapasha.auth_service.role.application.command.role;

import java.util.UUID;

public record CreateRoleCommand(
    UUID workspaceId,
    String name,
    String description,
    UUID invokedBy
) {
}
