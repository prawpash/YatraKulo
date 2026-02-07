package com.ekapasha.auth_service.role.application.query.role;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.repository.RoleReadRepository;
import com.ekapasha.auth_service.shared.application.query.QueryHandler;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPage;

public class SearchRolesQueryHandler implements QueryHandler<SearchRolesQuery, DomainPage<Role>> {
  private final RoleReadRepository roleReadRepository;

  public SearchRolesQueryHandler(
      RoleReadRepository roleReadRepository
  ) {
    this.roleReadRepository = roleReadRepository;
  }

  @Override
  public DomainPage<Role> handler(SearchRolesQuery query) {
    return this.roleReadRepository.getAll(query.searchTerm(), query.pageRequest(), query.includeDeleted());
  }
}
