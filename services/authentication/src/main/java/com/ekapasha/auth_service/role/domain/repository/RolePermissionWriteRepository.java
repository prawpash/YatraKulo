package com.ekapasha.auth_service.role.domain.repository;

import com.ekapasha.auth_service.role.domain.entity.RolePermission;
import com.ekapasha.auth_service.role.domain.enums.Permission;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RolePermissionWriteRepository {
  public void save(RolePermission rolePermission);

  public void deleteByRoleIdAndPermission(UUID roleId, Permission permission);

  public void saveAll(List<RolePermission> rolePermissions);

  public void deleteByRoleIdAndPermissions(UUID roleId, Set<Permission> permissions);
}
