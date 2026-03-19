package com.ekapasha.auth_service.workspace.infrastructure.persistence.mapper;

import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;

public class WorkspaceMemberMapper {
  public static WorkspaceMember toDomain(WorkspaceMemberEntity entity) {
    return WorkspaceMember.builder()
        .id(entity.getId())
        .workspaceId(entity.getWorkspaceId())
        .userId(entity.getUserId())
        .roleId(entity.getRoleId())
        .addedAt(entity.getAddedAt())
        .updatedAt(entity.getUpdatedAt())
        .addedBy(entity.getAddedBy())
        .updatedBy(entity.getUpdatedBy())
        .build();
  }

  public static WorkspaceMemberEntity toEntity(WorkspaceMember workspaceMember) {
    return new WorkspaceMemberEntity(
        workspaceMember.getId(),
        workspaceMember.getWorkspaceId(),
        workspaceMember.getUserId(),
        workspaceMember.getRoleId(),
        workspaceMember.getAddedAt(),
        workspaceMember.getUpdatedAt(),
        workspaceMember.getAddedBy().orElse(null),
        workspaceMember.getUpdatedBy().orElse(null));
  }
}
