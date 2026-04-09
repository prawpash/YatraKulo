package com.ekapasha.auth_service.workspace.application.query.workspacepermission;

import java.util.UUID;

public record ListWorkspacePermissionsQuery(
    UUID workspaceId,
    UUID userId
) {}