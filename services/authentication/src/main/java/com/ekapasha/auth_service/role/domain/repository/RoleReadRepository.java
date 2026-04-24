package com.ekapasha.auth_service.role.domain.repository;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.shared.pagination.DomainPage;
import com.ekapasha.shared.pagination.DomainPageRequest;

import java.util.Optional;
import java.util.UUID;

public interface RoleReadRepository {
  // Existing methods
  Optional<Role> findById(UUID id);

  long count();

  long count(String searchTerm);

  // Workspace-scoped methods
  Optional<Role> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

  long countByWorkspaceId(UUID workspaceId);

  long countByWorkspaceId(UUID workspaceId, String searchTerm);

  // Global roles methods (workspaceId = null)
  long countGlobalRoles();

  long countGlobalRoles(String searchTerm);

  DomainPage<Role> getRoles(UUID workspaceId, boolean includeGlobal, String searchTerm, DomainPageRequest pageRequest, boolean includeDeleted);
}
