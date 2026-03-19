package com.ekapasha.auth_service.role.infrastructure.persistence.mapper;

import com.ekapasha.auth_service.role.domain.entity.RolePermission;
import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.infrastructure.persistence.entity.RolePermissionEntity;

public class RolePermissionMapper {
  public static RolePermission toDomain(RolePermissionEntity entity) {
    return RolePermission.builder()
        .id(entity.getId())
        .roleId(entity.getRoleId())
        .permission(Permission.fromCode(entity.getPermissionCode()))
        .addedAt(entity.getAddedAt())
        .build();
  }

  public static RolePermissionEntity toEntity(RolePermission domain) {
    return new RolePermissionEntity(
        domain.getId(),
        domain.getRoleId(),
        domain.getPermission().getCode(),
        domain.getAddedAt()
    );
  }
}
