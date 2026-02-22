package com.ekapasha.auth_service.workspace.application.query.workspace;

import com.ekapasha.auth_service.shared.application.query.QueryHandler;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPage;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListWorkspacesByOwnerQueryHandler implements QueryHandler<ListWorkspacesByOwnerQuery, DomainPage<Workspace>> {

  private final WorkspaceReadRepository workspaceReadRepository;

  @Override
  public DomainPage<Workspace> handler(ListWorkspacesByOwnerQuery query) {
    return this.workspaceReadRepository.findByOwnerId(query.ownerId(), query.pageRequest());
  }
}
