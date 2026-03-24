package com.ekapasha.auth_service.workspace.application.command.workspacemember;

import java.util.UUID;

public record UpdateWorkspaceMemberRoleCommand(
    UUID workspaceId,
    UUID userId,
    UUID roleId,
    UUID invokedBy
) {}
