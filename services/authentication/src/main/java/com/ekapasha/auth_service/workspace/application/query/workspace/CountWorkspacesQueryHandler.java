package com.ekapasha.auth_service.workspace.application.query.workspace;

import com.ekapasha.shared.cqrs.QueryHandler;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CountWorkspacesQueryHandler implements QueryHandler<CountWorkspacesQuery, Long> {

  private final WorkspaceReadRepository workspaceReadRepository;

  @Override
  public Long handler(CountWorkspacesQuery query) {
    return this.workspaceReadRepository.count();
  }
}
