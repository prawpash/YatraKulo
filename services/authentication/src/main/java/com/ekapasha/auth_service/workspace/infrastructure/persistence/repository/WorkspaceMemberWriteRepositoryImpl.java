package com.ekapasha.auth_service.workspace.infrastructure.persistence.repository;

import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberWriteRepository;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.mapper.WorkspaceMemberMapper;

import java.util.UUID;

public class WorkspaceMemberWriteRepositoryImpl implements WorkspaceMemberWriteRepository {
  private final JPAWorkspaceMemberRepository workspaceMemberRepository;

  public WorkspaceMemberWriteRepositoryImpl(JPAWorkspaceMemberRepository workspaceMemberRepository) {
    this.workspaceMemberRepository = workspaceMemberRepository;
  }

  @Override
  public WorkspaceMember save(WorkspaceMember workspaceMember) {
    WorkspaceMemberEntity entity = WorkspaceMemberMapper.toEntity(workspaceMember);
    this.workspaceMemberRepository.save(entity);
    return WorkspaceMemberMapper.toDomain(entity);
  }

  @Override
  public void deleteById(UUID id) {
    this.workspaceMemberRepository.deleteById(id);
  }

  @Override
  public void deleteByWorkspaceIdAndUserId(UUID workspaceId, UUID userId) {
    this.workspaceMemberRepository.deleteByWorkspaceIdAndUserId(workspaceId, userId);
  }
}
