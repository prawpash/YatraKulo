package com.ekapasha.auth_service.role.application.query.permission;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.domain.repository.PermissionReadRepository;
import com.ekapasha.shared.cqrs.QueryHandler;
import com.ekapasha.shared.pagination.DomainPage;

public class ListPermissionsQueryHandler
    implements QueryHandler<ListPermissionsQuery, DomainPage<Permission>> {
  private final PermissionReadRepository permissionReadRepository;

  public ListPermissionsQueryHandler(PermissionReadRepository permissionReadRepository) {
    this.permissionReadRepository = permissionReadRepository;
  }

  @Override
  public DomainPage<Permission> handler(ListPermissionsQuery query) {
    return this.permissionReadRepository.getAll(query.search(), query.pageRequest());
  }
}
