package com.ekapasha.auth_service.role.infrastructure.persistence.repository;

import com.ekapasha.auth_service.role.domain.entity.RolePermission;
import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.domain.repository.RolePermissionWriteRepository;
import com.ekapasha.auth_service.role.infrastructure.persistence.mapper.RolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RolePermissionWriteRepositoryImpl implements RolePermissionWriteRepository {
  private final JPARolePermissionRepository jpaRepository;

  @Override
  public void save(RolePermission rolePermission) {
    jpaRepository.save(RolePermissionMapper.toEntity(rolePermission));
  }

  @Override
  public void deleteByRoleIdAndPermission(UUID roleId, Permission permission) {
    jpaRepository.deleteByRoleIdAndPermissionCode(roleId, permission.getCode());
  }

  @Override
  public void saveAll(List<RolePermission> rolePermissions) {
    jpaRepository.saveAll(
        rolePermissions.stream().map(RolePermissionMapper::toEntity).toList()
    );
  }

  @Override
  public void deleteByRoleIdAndPermissions(UUID roleId, Set<Permission> permissions) {
    jpaRepository.deleteByRoleIdAndPermissionCodeIn(
        roleId,
        permissions.stream().map(Permission::getCode).toList()
    );
  }
}
