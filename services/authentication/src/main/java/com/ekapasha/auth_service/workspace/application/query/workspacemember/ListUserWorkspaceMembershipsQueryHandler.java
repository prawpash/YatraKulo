package com.ekapasha.auth_service.workspace.application.query.workspacemember;

import com.ekapasha.shared.cqrs.QueryHandler;
import com.ekapasha.shared.pagination.DomainPage;
import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberReadRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListUserWorkspaceMembershipsQueryHandler implements QueryHandler<ListUserWorkspaceMembershipsQuery, DomainPage<WorkspaceMember>> {

  private final WorkspaceMemberReadRepository workspaceMemberReadRepository;

  @Override
  public DomainPage<WorkspaceMember> handler(ListUserWorkspaceMembershipsQuery query) {
    return this.workspaceMemberReadRepository.findByUserId(query.userId(), query.pageRequest());
  }
}
