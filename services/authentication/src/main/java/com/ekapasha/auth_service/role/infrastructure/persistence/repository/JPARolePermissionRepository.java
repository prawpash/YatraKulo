package com.ekapasha.auth_service.role.infrastructure.persistence.repository;

import com.ekapasha.auth_service.role.infrastructure.persistence.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JPARolePermissionRepository extends JpaRepository<RolePermissionEntity, UUID> {
  boolean existsByRoleIdAndPermissionCode(UUID roleId, String permissionCode);

  List<RolePermissionEntity> findByRoleId(UUID roleId);

  List<RolePermissionEntity> findByRoleIdAndPermissionCodeIn(UUID roleId, List<String> permissionCodes);

  int countByRoleId(UUID roleId);

  void deleteByRoleIdAndPermissionCodeIn(UUID roleId, List<String> permissionCodes);

  void deleteByRoleIdAndPermissionCode(UUID roleId, String permissionCode);
}
