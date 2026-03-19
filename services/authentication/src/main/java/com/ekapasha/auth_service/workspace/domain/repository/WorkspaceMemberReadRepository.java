package com.ekapasha.auth_service.workspace.domain.repository;

import com.ekapasha.auth_service.shared.domain.pagination.DomainPage;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPageRequest;
import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;

import java.util.Optional;
import java.util.UUID;

public interface WorkspaceMemberReadRepository {
  Optional<WorkspaceMember> findById(UUID id);

  Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

  DomainPage<WorkspaceMember> findByWorkspaceId(UUID workspaceId, DomainPageRequest pageRequest);

  DomainPage<WorkspaceMember> findByUserId(UUID userId, DomainPageRequest pageRequest);

  boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

  long countByWorkspaceId(UUID workspaceId);
}
