package com.ekapasha.auth_service.role.infrastructure.persistence.repository;

import com.ekapasha.auth_service.role.domain.entity.RolePermission;
import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.domain.repository.RolePermissionReadRepository;
import com.ekapasha.auth_service.role.infrastructure.persistence.mapper.RolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RolePermissionReadRepositoryImpl implements RolePermissionReadRepository {
  private final JPARolePermissionRepository jpaRepository;

  @Override
  public boolean exists(UUID roleId, Permission permission) {
    return jpaRepository.existsByRoleIdAndPermissionCode(roleId, permission.getCode());
  }

  @Override
  public Optional<RolePermission> findById(UUID id) {
    return jpaRepository.findById(id).map(RolePermissionMapper::toDomain);
  }

  @Override
  public List<RolePermission> findByRoleId(UUID roleId) {
    return jpaRepository.findByRoleId(roleId).stream().map(RolePermissionMapper::toDomain).toList();
  }

  @Override
  public List<RolePermission> findByRoleIdAndPermissionsIn(UUID roleId, Set<Permission> permissions) {
    List<String> permissionCodes = permissions.stream().map(Permission::getCode).toList();
    return jpaRepository.findByRoleIdAndPermissionCodeIn(roleId, permissionCodes).stream()
        .map(RolePermissionMapper::toDomain)
        .toList();
  }

  @Override
  public int countByRoleId(UUID roleId) {
    return jpaRepository.countByRoleId(roleId);
  }
}
