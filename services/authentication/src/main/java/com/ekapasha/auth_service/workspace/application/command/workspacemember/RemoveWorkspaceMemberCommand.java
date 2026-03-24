package com.ekapasha.auth_service.workspace.application.command.workspacemember;

import java.util.UUID;

public record RemoveWorkspaceMemberCommand(
    UUID workspaceId,
    UUID userId,
    UUID invokedBy
) {}
