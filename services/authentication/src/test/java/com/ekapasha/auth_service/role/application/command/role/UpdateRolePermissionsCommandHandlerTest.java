package com.ekapasha.auth_service.role.application.command.role;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.domain.repository.RoleReadRepository;
import com.ekapasha.auth_service.role.domain.service.RolePermissionService;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateRolePermissionsCommandHandlerTest {

  @Mock private RoleReadRepository roleReadRepository;
  @Mock private WorkspaceReadRepository workspaceReadRepository;
  @Mock private RolePermissionService rolePermissionService;
  @InjectMocks private UpdateRolePermissionsCommandHandler handler;

  @Test
  void shouldGrantAndRevokePermissionsWhenAuthorized() {
    Role role = activeWorkspaceRole();
    when(roleReadRepository.findById(ROLE_ID)).thenReturn(java.util.Optional.of(role));
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.of(workspace()));

    handler.handler(
        new UpdateRolePermissionsCommand(
            ROLE_ID,
            Set.of(Permission.WORKSPACE_UPDATE),
            Set.of(Permission.WORKSPACE_DELETE),
            OWNER_ID));

    verify(rolePermissionService).grantPermissions(
        eq(role), eq(Set.of(Permission.WORKSPACE_UPDATE)), any());
    verify(rolePermissionService).revokePermissions(role, Set.of(Permission.WORKSPACE_DELETE));
  }

  @Test
  void shouldThrowWhenRoleMissing() {
    when(roleReadRepository.findById(ROLE_ID)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(
            () ->
                handler.handler(
                    new UpdateRolePermissionsCommand(ROLE_ID, Set.of(), Set.of(), OWNER_ID)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Role not found.");
  }

  @Test
  void shouldThrowWhenRoleIsGlobal() {
    when(roleReadRepository.findById(ROLE_ID)).thenReturn(java.util.Optional.of(globalRole()));

    assertThatThrownBy(
            () ->
                handler.handler(
                    new UpdateRolePermissionsCommand(ROLE_ID, Set.of(), Set.of(), OWNER_ID)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Role not found.");
  }

  @Test
  void shouldThrowWhenCallerIsNotWorkspaceOwner() {
    Role role = activeWorkspaceRole();
    when(roleReadRepository.findById(ROLE_ID)).thenReturn(java.util.Optional.of(role));
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.of(workspace()));

    assertThatThrownBy(
            () ->
                handler.handler(
                    new UpdateRolePermissionsCommand(ROLE_ID, Set.of(), Set.of(), OTHER_USER_ID)))
        .isInstanceOf(UnauthorizedAccessException.class)
        .hasMessage("Only workspace owner can update role permissions.");
    verify(rolePermissionService, never()).grantPermissions(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anySet(), org.mockito.ArgumentMatchers.any());
    verify(rolePermissionService, never()).revokePermissions(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anySet());
  }

  @Test
  void shouldNotCallPermissionServiceWhenAssignAndRevokeAreEmpty() {
    Role role = activeWorkspaceRole();
    when(roleReadRepository.findById(ROLE_ID)).thenReturn(java.util.Optional.of(role));
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.of(workspace()));

    handler.handler(new UpdateRolePermissionsCommand(ROLE_ID, Set.of(), Set.of(), OWNER_ID));

    verify(rolePermissionService, never()).grantPermissions(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anySet(), org.mockito.ArgumentMatchers.any());
    verify(rolePermissionService, never()).revokePermissions(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anySet());
  }
}
