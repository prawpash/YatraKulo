package com.ekapasha.auth_service.workspace.application.command.workspace;

import java.util.UUID;

public record CreateWorkspaceCommand(
    String name,
    String description,
    UUID ownerId,
    boolean isDefault,
    UUID invokedBy
) {}
