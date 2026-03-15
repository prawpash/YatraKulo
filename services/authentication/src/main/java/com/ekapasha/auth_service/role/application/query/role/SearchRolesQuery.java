package com.ekapasha.auth_service.role.application.query.role;

import com.ekapasha.auth_service.shared.domain.pagination.DomainPageRequest;

import java.util.UUID;

public record SearchRolesQuery(
    UUID workspaceId,
    String searchTerm,
    DomainPageRequest pageRequest,
    Boolean includeDeleted
) { }
