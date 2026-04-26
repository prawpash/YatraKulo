package com.ekapasha.auth_service.workspace.application.query.workspacemember;

import com.ekapasha.shared.cqrs.QueryHandler;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberReadRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CountWorkspaceMembersQueryHandler implements QueryHandler<CountWorkspaceMembersQuery, Long> {

  private final WorkspaceMemberReadRepository workspaceMemberReadRepository;

  @Override
  public Long handler(CountWorkspaceMembersQuery query) {
    return this.workspaceMemberReadRepository.countByWorkspaceId(query.workspaceId());
  }
}
