package com.ekapasha.auth_service.workspace.application.command.workspace;

import java.util.UUID;

public record SetWorkspaceDefaultCommand(
    UUID id,
    boolean isDefault,
    UUID invokedBy
) {}
