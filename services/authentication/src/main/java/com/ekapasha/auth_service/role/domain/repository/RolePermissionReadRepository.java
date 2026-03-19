package com.ekapasha.auth_service.role.domain.repository;

import com.ekapasha.auth_service.role.domain.entity.RolePermission;
import com.ekapasha.auth_service.role.domain.enums.Permission;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RolePermissionReadRepository {
  public boolean exists(UUID roleId, Permission permission);

  public Optional<RolePermission> findById(UUID id);

  public List<RolePermission> findByRoleId(UUID roleId);

  public List<RolePermission> findByRoleIdAndPermissionsIn(UUID roleId, Set<Permission> permissions);

  public int countByRoleId(UUID roleId);
}
