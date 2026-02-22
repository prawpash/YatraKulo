package com.ekapasha.auth_service.workspace.application.command.workspace;

import java.util.UUID;

public record UpdateWorkspaceCommand(
    UUID id,
    String name,
    String description,
    UUID invokedBy
) {}
