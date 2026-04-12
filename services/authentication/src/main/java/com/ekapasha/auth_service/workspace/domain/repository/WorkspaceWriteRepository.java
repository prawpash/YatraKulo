package com.ekapasha.auth_service.workspace.domain.repository;

import com.ekapasha.auth_service.workspace.domain.entity.Workspace;

import java.util.UUID;

public interface WorkspaceWriteRepository {
  Workspace save(Workspace workspace);

  void setDefault(UUID workspaceId, UUID ownerId);

  void deleteById(UUID id);
}
