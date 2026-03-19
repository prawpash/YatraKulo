package com.ekapasha.auth_service.workspace.application.query.workspacemember;

import com.ekapasha.auth_service.shared.domain.pagination.DomainPageRequest;

import java.util.UUID;

public record ListUserWorkspaceMembershipsQuery(
    UUID userId,
    DomainPageRequest pageRequest
) {}
