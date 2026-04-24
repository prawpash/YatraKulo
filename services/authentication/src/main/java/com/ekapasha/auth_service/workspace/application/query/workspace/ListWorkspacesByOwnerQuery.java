package com.ekapasha.auth_service.workspace.application.query.workspace;

import com.ekapasha.shared.pagination.DomainPageRequest;

import java.util.UUID;

public record ListWorkspacesByOwnerQuery(
    UUID ownerId, DomainPageRequest pageRequest, String search) {}
