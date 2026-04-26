package com.ekapasha.auth_service.workspace.application.query.workspace;

import com.ekapasha.shared.cqrs.QueryHandler;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetWorkspaceByIdAndOwnerIdQueryHandler implements QueryHandler<GetWorkspaceByIdAndOwnerIdQuery, Workspace> {

  private final WorkspaceReadRepository workspaceReadRepository;

  @Override
  public Workspace handler(GetWorkspaceByIdAndOwnerIdQuery query) {
    return this.workspaceReadRepository
        .findByIdAndOwnerId(query.id(), query.ownerId())
        .orElseThrow(() -> new NotFoundException("Workspace not found for the given owner"));
  }
}
