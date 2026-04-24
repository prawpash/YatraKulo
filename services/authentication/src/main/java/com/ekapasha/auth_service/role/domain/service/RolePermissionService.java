package com.ekapasha.auth_service.role.domain.service;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.entity.RolePermission;
import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.domain.repository.*;
import com.ekapasha.shared.exception.DomainRuleViolationException;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RolePermissionService {
  private final RolePermissionReadRepository rolePermissionReadRepository;
  private final RolePermissionWriteRepository rolePermissionWriteRepository;

  public RolePermissionService(
      RolePermissionReadRepository rolePermissionReadRepository,
      RolePermissionWriteRepository rolePermissionWriteRepository) {
    this.rolePermissionReadRepository = rolePermissionReadRepository;
    this.rolePermissionWriteRepository = rolePermissionWriteRepository;
  }

  public void grantPermission(Role role, Permission permission, Instant addedAt) {
    // check if the role data is deleted
    if (role.getDeletedAt().isPresent()) {
      throw new DomainRuleViolationException("Role is already deleted.");
    }

    // check if the permission is already granted
    if (this.rolePermissionReadRepository.exists(role.getId(), permission)) {
      return;
    }

    RolePermission rolePermission =
        RolePermission.builder()
            .id(UUID.randomUUID())
            .roleId(role.getId())
            .permission(permission)
            .addedAt(addedAt)
            .build();

    this.rolePermissionWriteRepository.save(rolePermission);
  }

  public void revokePermission(Role role, Permission permission) {
    // check if the role data is deleted
    if (role.getDeletedAt().isPresent()) {
      throw new DomainRuleViolationException("Role is already deleted.");
    }

    if (!this.rolePermissionReadRepository.exists(role.getId(), permission)) {
      return;
    }

    this.rolePermissionWriteRepository.deleteByRoleIdAndPermission(role.getId(), permission);
  }

  public void grantPermissions(Role role, Set<Permission> permissions, Instant addedAt) {
    if (role.getDeletedAt().isPresent()) {
      throw new DomainRuleViolationException("Role is already deleted.");
    }

    Set<Permission> existingPermissions =
        this.rolePermissionReadRepository
            .findByRoleIdAndPermissionsIn(role.getId(), permissions)
            .stream()
            .map(RolePermission::getPermission)
            .collect(Collectors.toSet());

    List<RolePermission> rolePermissions =
        permissions.stream()
            .filter(permission -> !existingPermissions.contains(permission))
            .map(
                permission ->
                    RolePermission.builder()
                        .id(UUID.randomUUID())
                        .roleId(role.getId())
                        .permission(permission)
                        .addedAt(addedAt)
                        .build())
            .toList();

    if (!rolePermissions.isEmpty()) {
      this.rolePermissionWriteRepository.saveAll(rolePermissions);
    }
  }

  public void revokePermissions(Role role, Set<Permission> permissions) {
    if (role.getDeletedAt().isPresent()) {
      throw new DomainRuleViolationException("Role is already deleted.");
    }

    Set<Permission> existingPermissions =
        this.rolePermissionReadRepository
            .findByRoleIdAndPermissionsIn(role.getId(), permissions)
            .stream()
            .map(RolePermission::getPermission)
            .collect(Collectors.toSet());

    if (!existingPermissions.isEmpty()) {
      this.rolePermissionWriteRepository.deleteByRoleIdAndPermissions(
          role.getId(), existingPermissions);
    }
  }
}
