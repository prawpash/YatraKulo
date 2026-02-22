package com.ekapasha.auth_service.workspace.application.query.workspace;

import java.util.UUID;

public record GetDefaultWorkspaceByOwnerQuery(
    UUID ownerId
) {}
