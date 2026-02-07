package com.ekapasha.auth_service.role.application.query.role;

import com.ekapasha.auth_service.shared.domain.pagination.DomainPageRequest;

public record SearchRolesQuery(
    String searchTerm,
    DomainPageRequest pageRequest,
    Boolean includeDeleted
) { }
