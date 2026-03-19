package com.ekapasha.auth_service.role.application.command.role;

import com.ekapasha.auth_service.role.domain.enums.Permission;

import java.util.Set;
import java.util.UUID;

public record UpdateRolePermissionsCommand(
    UUID roleId,
    Set<Permission> assign,
    Set<Permission> revoke,
    UUID invokedBy
) {
}
