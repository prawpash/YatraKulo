package com.ekapasha.auth_service.workspace.domain.repository;

import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;

import java.util.UUID;

public interface WorkspaceMemberWriteRepository {
  WorkspaceMember save(WorkspaceMember member);

  void deleteById(UUID id);

  void deleteByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);
}
