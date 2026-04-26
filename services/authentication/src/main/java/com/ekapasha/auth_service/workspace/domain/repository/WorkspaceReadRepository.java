package com.ekapasha.auth_service.workspace.domain.repository;

import com.ekapasha.shared.pagination.DomainPage;
import com.ekapasha.shared.pagination.DomainPageRequest;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceReadRepository {
  Optional<Workspace> findById(UUID id);

  Optional<Workspace> findByIdAndOwnerId(UUID id, UUID ownerId);

  List<Workspace> findByOwnerId(UUID ownerId);

  DomainPage<Workspace> findByOwnerId(UUID ownerId, DomainPageRequest pageRequest);

  DomainPage<Workspace> findByOwnerIdAndSearch(
      UUID ownerId, String search, DomainPageRequest pageRequest);

  Optional<Workspace> findDefaultByOwnerId(UUID ownerId);

  boolean existsByIdAndOwnerId(UUID id, UUID ownerId);

  long count();
}
