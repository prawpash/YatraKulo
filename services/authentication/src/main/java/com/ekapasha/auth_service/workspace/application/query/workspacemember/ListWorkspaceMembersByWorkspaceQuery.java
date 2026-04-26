package com.ekapasha.auth_service.workspace.application.query.workspacemember;

import com.ekapasha.shared.pagination.DomainPageRequest;

import java.util.UUID;

public record ListWorkspaceMembersByWorkspaceQuery(
    UUID workspaceId,
    DomainPageRequest pageRequest,
    String search
) {}
