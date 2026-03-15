package com.ekapasha.auth_service.role.domain.repository;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPage;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPageRequest;

import java.util.Optional;
import java.util.UUID;

public interface RoleReadRepository {
  // Existing methods
  Optional<Role> findById(UUID id);

  DomainPage<Role> getAll(DomainPageRequest pageRequest, Boolean includeDeleted);

  DomainPage<Role> getAll(String searchTerm, DomainPageRequest pageRequest, Boolean includeDeleted);

  long count();

  long count(String searchTerm);

  // Workspace-scoped methods
  Optional<Role> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

  DomainPage<Role> getAllByWorkspaceId(UUID workspaceId, DomainPageRequest pageRequest, Boolean includeDeleted);

  DomainPage<Role> getAllByWorkspaceId(UUID workspaceId, String searchTerm, DomainPageRequest pageRequest, Boolean includeDeleted);

  long countByWorkspaceId(UUID workspaceId);

  long countByWorkspaceId(UUID workspaceId, String searchTerm);

  // Global roles methods (workspaceId = null)
  DomainPage<Role> getGlobalRoles(DomainPageRequest pageRequest, Boolean includeDeleted);

  DomainPage<Role> getGlobalRoles(String searchTerm, DomainPageRequest pageRequest, Boolean includeDeleted);

  long countGlobalRoles();

  long countGlobalRoles(String searchTerm);
}
