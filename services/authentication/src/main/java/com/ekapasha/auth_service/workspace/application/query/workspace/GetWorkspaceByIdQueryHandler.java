package com.ekapasha.auth_service.workspace.application.query.workspace;

import com.ekapasha.auth_service.shared.application.query.QueryHandler;
import com.ekapasha.auth_service.shared.domain.exception.NotFoundException;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetWorkspaceByIdQueryHandler implements QueryHandler<GetWorkspaceByIdQuery, Workspace> {

  private final WorkspaceReadRepository workspaceReadRepository;

  @Override
  public Workspace handler(GetWorkspaceByIdQuery query) {
    return this.workspaceReadRepository
        .findById(query.id())
        .orElseThrow(() -> new NotFoundException("Workspace not found"));
  }
}
