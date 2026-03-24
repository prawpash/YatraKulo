package com.ekapasha.auth_service.workspace.application.command.workspace;

import java.util.UUID;

public record DeleteWorkspaceCommand(
    UUID id,
    UUID invokedBy
) {}
