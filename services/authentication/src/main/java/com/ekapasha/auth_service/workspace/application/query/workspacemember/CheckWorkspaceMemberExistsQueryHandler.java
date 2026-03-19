package com.ekapasha.auth_service.workspace.application.query.workspacemember;

import com.ekapasha.auth_service.shared.application.query.QueryHandler;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberReadRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CheckWorkspaceMemberExistsQueryHandler implements QueryHandler<CheckWorkspaceMemberExistsQuery, Boolean> {

  private final WorkspaceMemberReadRepository workspaceMemberReadRepository;

  @Override
  public Boolean handler(CheckWorkspaceMemberExistsQuery query) {
    return this.workspaceMemberReadRepository.existsByWorkspaceIdAndUserId(query.workspaceId(), query.userId());
  }
}
