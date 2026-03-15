package com.ekapasha.auth_service.role.application.query.role;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.repository.RoleReadRepository;
import com.ekapasha.auth_service.shared.application.query.QueryHandler;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPage;

public class ListRolesQueryHandler implements QueryHandler<ListRolesQuery, DomainPage<Role>> {
  private final RoleReadRepository roleReadRepository;

  public ListRolesQueryHandler(
      RoleReadRepository roleReadRepository
  ){
    this.roleReadRepository = roleReadRepository;
  }

  @Override
  public DomainPage<Role> handler(ListRolesQuery query) {
    // If workspaceId is null, return global roles (workspaceId = null)
    if (query.workspaceId() == null) {
      return this.roleReadRepository.getGlobalRoles(
          query.pageRequest(),
          query.includeDeleted()
      );
    }
    
    return this.roleReadRepository.getAllByWorkspaceId(
        query.workspaceId(),
        query.pageRequest(),
        query.includeDeleted()
    );
  }
}
