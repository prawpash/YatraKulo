package com.ekapasha.auth_service.workspace.infrastructure.persistence.repository;

import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceWriteRepository;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.mapper.WorkspaceMapper;

import java.util.UUID;

public class WorkspaceWriteRepositoryImpl implements WorkspaceWriteRepository {
  private final JPAWorkspaceRepository workspaceRepository;

  public WorkspaceWriteRepositoryImpl(JPAWorkspaceRepository workspaceRepository) {
    this.workspaceRepository = workspaceRepository;
  }

  @Override
  public Workspace save(Workspace workspace) {
    WorkspaceEntity entity = WorkspaceMapper.toEntity(workspace);
    this.workspaceRepository.save(entity);
    return WorkspaceMapper.toDomain(entity);
  }

  @Override
  public void deleteById(UUID id) {
    this.workspaceRepository.deleteById(id);
  }

  @Override
  public void setDefault(UUID workspaceId, UUID ownerId) {
    this.workspaceRepository.setDefault(workspaceId, ownerId);
  }
}
