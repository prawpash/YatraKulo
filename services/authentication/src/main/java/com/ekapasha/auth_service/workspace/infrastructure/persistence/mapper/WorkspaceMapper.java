package com.ekapasha.auth_service.workspace.infrastructure.persistence.mapper;

import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.entity.WorkspaceEntity;

public class WorkspaceMapper {
  public static Workspace toDomain(WorkspaceEntity entity) {
    return Workspace.builder()
        .id(entity.getId())
        .name(entity.getName())
        .description(entity.getDescription())
        .ownerId(entity.getOwnerId())
        .isDefault(entity.isDefault())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();
  }

  public static WorkspaceEntity toEntity(Workspace workspace) {
    return new WorkspaceEntity(
        workspace.getId(),
        workspace.getName(),
        workspace.getDescription().orElse(null),
        workspace.getOwnerId(),
        workspace.isDefault(),
        workspace.getCreatedAt(),
        workspace.getUpdatedAt());
  }
}
