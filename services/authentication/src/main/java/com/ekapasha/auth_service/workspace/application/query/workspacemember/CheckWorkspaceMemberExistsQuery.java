package com.ekapasha.auth_service.workspace.application.query.workspacemember;

import java.util.UUID;

public record CheckWorkspaceMemberExistsQuery(
    UUID workspaceId,
    UUID userId
) {}
