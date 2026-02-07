package com.ekapasha.auth_service.role.application.query.permission;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.domain.repository.PermissionReadRepository;
import com.ekapasha.auth_service.shared.application.query.QueryHandler;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPage;

public class SearchPermissionsQueryHandler implements QueryHandler<SearchPermissionsQuery, DomainPage<Permission>> {
  private final PermissionReadRepository permissionReadRepository;

  public SearchPermissionsQueryHandler(
      PermissionReadRepository permissionReadRepository
  ) {
    this.permissionReadRepository = permissionReadRepository;
  }

  @Override
  public DomainPage<Permission> handler(SearchPermissionsQuery query) {
    return this.permissionReadRepository.getAll(query.searchTerm(), query.pageRequest());
  }
}
