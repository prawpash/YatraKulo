package com.ekapasha.auth_service.workspace.application.query.workspace;

import com.ekapasha.auth_service.shared.application.query.QueryHandler;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CheckWorkspaceExistsQueryHandler implements QueryHandler<CheckWorkspaceExistsQuery, Boolean> {

  private final WorkspaceReadRepository workspaceReadRepository;

  @Override
  public Boolean handler(CheckWorkspaceExistsQuery query) {
    return this.workspaceReadRepository.existsByIdAndOwnerId(query.id(), query.ownerId());
  }
}
