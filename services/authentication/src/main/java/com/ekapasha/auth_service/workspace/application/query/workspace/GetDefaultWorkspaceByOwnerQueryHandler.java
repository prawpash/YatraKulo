package com.ekapasha.auth_service.workspace.application.query.workspace;

import com.ekapasha.shared.cqrs.QueryHandler;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetDefaultWorkspaceByOwnerQueryHandler
    implements QueryHandler<GetDefaultWorkspaceByOwnerQuery, Workspace> {

  private final WorkspaceReadRepository workspaceReadRepository;

  @Override
  public Workspace handler(GetDefaultWorkspaceByOwnerQuery query) {
    return this.workspaceReadRepository
        .findDefaultByOwnerId(query.ownerId())
        .orElseThrow(
            () -> new NotFoundException("Default workspace not found for the given owner"));
  }
}
