package com.ekapasha.auth_service.workspace.application.query.workspacemember;

import com.ekapasha.shared.cqrs.QueryHandler;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberReadRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetWorkspaceMemberByWorkspaceAndUserQueryHandler implements QueryHandler<GetWorkspaceMemberByWorkspaceAndUserQuery, WorkspaceMember> {

  private final WorkspaceMemberReadRepository workspaceMemberReadRepository;

  @Override
  public WorkspaceMember handler(GetWorkspaceMemberByWorkspaceAndUserQuery query) {
    return this.workspaceMemberReadRepository
        .findByWorkspaceIdAndUserId(query.workspaceId(), query.userId())
        .orElseThrow(() -> new NotFoundException("Workspace member not found"));
  }
}
