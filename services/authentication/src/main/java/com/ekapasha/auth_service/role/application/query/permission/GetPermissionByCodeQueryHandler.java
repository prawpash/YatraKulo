package com.ekapasha.auth_service.role.application.query.permission;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.domain.repository.PermissionReadRepository;
import com.ekapasha.shared.cqrs.QueryHandler;
import com.ekapasha.shared.exception.NotFoundException;

public class GetPermissionByCodeQueryHandler implements QueryHandler<GetPermissionByCodeQuery, Permission> {
  private final PermissionReadRepository permissionRepository;

  public GetPermissionByCodeQueryHandler(
      PermissionReadRepository permissionRepository
  ) {
    this.permissionRepository = permissionRepository;
  }

  @Override
  public Permission handler(GetPermissionByCodeQuery query) {
    return this.permissionRepository.findByCode(query.code())
                                    .orElseThrow(() -> new NotFoundException("Permission not found"));
  }
}
