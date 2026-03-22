package com.ekapasha.auth_service.role.application.query.permission;

import com.ekapasha.auth_service.shared.domain.pagination.DomainPageRequest;

public record ListPermissionsQuery(String search, DomainPageRequest pageRequest) {}
