package com.ekapasha.auth_service.role.presentation.dto.role;

import com.ekapasha.auth_service.role.application.command.role.UpdateRolePermissionsCommand;
import com.ekapasha.auth_service.role.domain.enums.Permission;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UpdateRolePermissionsRequestDto(
    Set<String> assign,
    Set<String> revoke
) {
  public UpdateRolePermissionsCommand toCommand(UUID roleId, UUID invokedBy) {
    Set<Permission> assignPermissions = (assign == null) ? new HashSet<>() : assign.stream()
        .map(Permission::fromCode)
        .collect(Collectors.toSet());

    Set<Permission> revokePermissions = (revoke == null) ? new HashSet<>() : revoke.stream()
        .map(Permission::fromCode)
        .collect(Collectors.toSet());

    return new UpdateRolePermissionsCommand(roleId, assignPermissions, revokePermissions, invokedBy);
  }
}
