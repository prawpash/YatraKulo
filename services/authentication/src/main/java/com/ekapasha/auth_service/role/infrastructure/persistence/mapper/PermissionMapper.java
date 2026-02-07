package com.ekapasha.auth_service.role.infrastructure.persistence.mapper;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.infrastructure.persistence.entity.PermissionEntity;

public class PermissionMapper {
  public static Permission toDomain(PermissionEntity permissionEntity) {
    return Permission.fromCode(permissionEntity.getCode());
  }

  public static PermissionEntity toEntity(Permission permission) {
    return new PermissionEntity(permission.getCode(), permission.getDescription());
  }
}
