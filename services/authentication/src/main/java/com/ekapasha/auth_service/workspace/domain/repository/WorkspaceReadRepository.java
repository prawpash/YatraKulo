package com.ekapasha.auth_service.workspace.domain.repository;

import com.ekapasha.auth_service.shared.domain.pagination.DomainPage;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPageRequest;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceReadRepository {
  Optional<Workspace> findById(UUID id);
  
  Optional<Workspace> findByIdAndOwnerId(UUID id, UUID ownerId);
  
  List<Workspace> findByOwnerId(UUID ownerId);
  
  DomainPage<Workspace> findByOwnerId(UUID ownerId, DomainPageRequest pageRequest);
  
  Optional<Workspace> findDefaultByOwnerId(UUID ownerId);
  
  boolean existsByIdAndOwnerId(UUID id, UUID ownerId);
  
  long count();
}
