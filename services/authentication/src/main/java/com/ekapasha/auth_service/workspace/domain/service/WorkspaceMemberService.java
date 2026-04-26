package com.ekapasha.auth_service.workspace.domain.service;

import com.ekapasha.shared.exception.DomainRuleViolationException;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.exception.UnauthorizedAccessException;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberReadRepository;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberWriteRepository;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class WorkspaceMemberService {
  private final WorkspaceMemberReadRepository workspaceMemberReadRepository;
  private final WorkspaceMemberWriteRepository workspaceMemberWriteRepository;

  public void addMember(UUID workspaceId, UUID userId, UUID roleId, Instant addedAt, UUID addedBy) {
    // check if the member already exists
    if (this.workspaceMemberReadRepository.existsByWorkspaceIdAndUserId(workspaceId, userId)) {
      return;
    }

    WorkspaceMember workspaceMember = WorkspaceMember.builder()
        .id(UUID.randomUUID())
        .workspaceId(workspaceId)
        .userId(userId)
        .roleId(roleId)
        .addedAt(addedAt)
        .updatedAt(addedAt)
        .addedBy(addedBy)
        .updatedBy(addedBy)
        .build();

    this.workspaceMemberWriteRepository.save(workspaceMember);
  }

  public void removeMember(UUID workspaceId, UUID userId) {
    // check if the member exists
    WorkspaceMember member = this.workspaceMemberReadRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
        .orElseThrow(() -> new NotFoundException("User is not a member of this workspace."));

    // check if the member is the owner (addedBy is null)
    if (member.getAddedBy().isEmpty()) {
      throw new DomainRuleViolationException("Cannot remove the owner of the workspace.");
    }

    this.workspaceMemberWriteRepository.deleteByWorkspaceIdAndUserId(workspaceId, userId);
  }

  public void updateMemberRole(UUID workspaceId, UUID userId, UUID roleId, Instant updatedAt, UUID updatedBy) {
    // check if the member exists
    WorkspaceMember member = this.workspaceMemberReadRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
        .orElseThrow(() -> new NotFoundException("User is not a member of this workspace."));

    member.changeRole(roleId, updatedAt, updatedBy);

    this.workspaceMemberWriteRepository.save(member);
  }
}
