package com.ekapasha.auth_service.role.application.command.role;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.repository.RoleReadRepository;
import com.ekapasha.auth_service.role.domain.service.RolePermissionService;
import com.ekapasha.shared.cqrs.VoidCommandHandler;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.exception.UnauthorizedAccessException;
import com.ekapasha.shared.logging.AppLogger;
import com.ekapasha.shared.logging.LogEvent;
import com.ekapasha.auth_service.logging.AuthLogEvent;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
public class UpdateRolePermissionsCommandHandler
    implements VoidCommandHandler<UpdateRolePermissionsCommand> {

  private final RoleReadRepository roleReadRepository;
  private final WorkspaceReadRepository workspaceReadRepository;
  private final RolePermissionService rolePermissionService;
  private final AppLogger logger = new AppLogger(UpdateRolePermissionsCommandHandler.class);

  @Override
  @Transactional
  public void handler(UpdateRolePermissionsCommand command) {
    try {
      Role role =
          roleReadRepository
              .findById(command.roleId())
              .orElseThrow(() -> new NotFoundException("Role not found."));

      if (role.getWorkspaceId().isEmpty()) {
        throw new NotFoundException("Role not found.");
      }

      // Check permissions - only workspace owner can update roles for now
      Workspace workspace =
          workspaceReadRepository
              .findById(role.getWorkspaceId().get())
              .orElseThrow(() -> new NotFoundException("Workspace not found."));

      if (!workspace.getOwnerId().equals(command.invokedBy())) {
        throw new UnauthorizedAccessException("Only workspace owner can update role permissions.");
      }

      if (!command.assign().isEmpty()) {
        rolePermissionService.grantPermissions(role, command.assign(), Instant.now());
      }

      if (!command.revoke().isEmpty()) {
        rolePermissionService.revokePermissions(role, command.revoke());
      }

      this.logger.info(
          LogEvent.builder("Role permissions updated successfully for role: " + role.getName())
              .eventName(AuthLogEvent.ROLE_PERMISSIONS_UPDATED)
              .metadata("roleId", role.getId().toString())
              .metadata("name", role.getName())
              .metadata("assignedCount", command.assign().size())
              .metadata("revokedCount", command.revoke().size())
              .build());
    } catch (Exception e) {
      String roleId = command.roleId() != null ? command.roleId().toString() : "null";
      if (e instanceof NotFoundException || e instanceof UnauthorizedAccessException) {
        this.logger.warn(
            LogEvent.builder("Role permissions update failed: " + e.getMessage())
                .eventName(AuthLogEvent.ROLE_OPERATION_FAILED)
                .metadata("roleId", roleId)
                .error(e)
                .build());
      } else {
        this.logger.error(
            LogEvent.builder("Role permissions update failed with system error: " + e.getMessage())
                .eventName(AuthLogEvent.ROLE_OPERATION_FAILED)
                .metadata("roleId", roleId)
                .error(e)
                .build());
      }
      throw e;
    }
  }
}
