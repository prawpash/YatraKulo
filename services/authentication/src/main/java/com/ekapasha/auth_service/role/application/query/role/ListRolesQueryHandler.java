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
    return this.roleReadRepository.getAll(query.pageRequest(), query.includeDeleted());
  }
}
