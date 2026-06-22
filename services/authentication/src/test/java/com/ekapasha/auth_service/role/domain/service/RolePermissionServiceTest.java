package com.ekapasha.auth_service.role.domain.service;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.entity.RolePermission;
import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.domain.repository.RolePermissionReadRepository;
import com.ekapasha.auth_service.role.domain.repository.RolePermissionWriteRepository;
import com.ekapasha.shared.exception.DomainRuleViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolePermissionServiceTest {

  @Mock private RolePermissionReadRepository rolePermissionReadRepository;
  @Mock private RolePermissionWriteRepository rolePermissionWriteRepository;
  @InjectMocks private RolePermissionService service;
  @Captor private ArgumentCaptor<List<RolePermission>> rolePermissionListCaptor;

  @Test
  void shouldGrantPermissionWhenMissing() {
    Role role = activeWorkspaceRole();
    when(rolePermissionReadRepository.exists(ROLE_ID, Permission.WORKSPACE_UPDATE)).thenReturn(false);

    service.grantPermission(role, Permission.WORKSPACE_UPDATE, CREATED_AT);

    ArgumentCaptor<RolePermission> captor = ArgumentCaptor.forClass(RolePermission.class);
    verify(rolePermissionWriteRepository).save(captor.capture());
    RolePermission saved = captor.getValue();
    assertThat(saved.getRoleId()).isEqualTo(ROLE_ID);
    assertThat(saved.getPermission()).isEqualTo(Permission.WORKSPACE_UPDATE);
    assertThat(saved.getAddedAt()).isEqualTo(CREATED_AT);
  }

  @Test
  void shouldSkipGrantWhenPermissionAlreadyExists() {
    Role role = activeWorkspaceRole();
    when(rolePermissionReadRepository.exists(ROLE_ID, Permission.WORKSPACE_UPDATE)).thenReturn(true);

    service.grantPermission(role, Permission.WORKSPACE_UPDATE, CREATED_AT);

    verify(rolePermissionWriteRepository, never()).save(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void shouldRevokePermissionWhenPresent() {
    Role role = activeWorkspaceRole();
    when(rolePermissionReadRepository.exists(ROLE_ID, Permission.WORKSPACE_DELETE)).thenReturn(true);

    service.revokePermission(role, Permission.WORKSPACE_DELETE);

    verify(rolePermissionWriteRepository).deleteByRoleIdAndPermission(ROLE_ID, Permission.WORKSPACE_DELETE);
  }

  @Test
  void shouldSkipRevokeWhenPermissionIsAbsent() {
    Role role = activeWorkspaceRole();
    when(rolePermissionReadRepository.exists(ROLE_ID, Permission.WORKSPACE_DELETE)).thenReturn(false);

    service.revokePermission(role, Permission.WORKSPACE_DELETE);

    verify(rolePermissionWriteRepository, never()).deleteByRoleIdAndPermission(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
  }

  @Test
  void shouldGrantMultiplePermissionsWhileSkippingExistingOnes() {
    Role role = activeWorkspaceRole();
    Set<Permission> requested = Set.of(Permission.WORKSPACE_UPDATE, Permission.WORKSPACE_DELETE, Permission.ACCOUNT_READ);
    when(rolePermissionReadRepository.findByRoleIdAndPermissionsIn(ROLE_ID, requested))
        .thenReturn(List.of(rolePermission(Permission.WORKSPACE_UPDATE)));

    service.grantPermissions(role, requested, CREATED_AT);

    verify(rolePermissionWriteRepository).saveAll(rolePermissionListCaptor.capture());
    List<RolePermission> saved = rolePermissionListCaptor.getValue();
    assertThat(saved).extracting(RolePermission::getPermission)
        .containsExactlyInAnyOrder(Permission.WORKSPACE_DELETE, Permission.ACCOUNT_READ);
  }

  @Test
  void shouldNotGrantMultiplePermissionsWhenAllAlreadyExist() {
    Role role = activeWorkspaceRole();
    Set<Permission> requested = Set.of(Permission.WORKSPACE_UPDATE);
    when(rolePermissionReadRepository.findByRoleIdAndPermissionsIn(ROLE_ID, requested))
        .thenReturn(List.of(rolePermission(Permission.WORKSPACE_UPDATE)));

    service.grantPermissions(role, requested, CREATED_AT);

    verify(rolePermissionWriteRepository, never()).saveAll(org.mockito.ArgumentMatchers.anyList());
  }

  @Test
  void shouldRevokeMultiplePermissionsOnlyForExistingOnes() {
    Role role = activeWorkspaceRole();
    Set<Permission> requested = Set.of(Permission.WORKSPACE_UPDATE, Permission.WORKSPACE_DELETE, Permission.ACCOUNT_READ);
    when(rolePermissionReadRepository.findByRoleIdAndPermissionsIn(ROLE_ID, requested))
        .thenReturn(List.of(rolePermission(Permission.WORKSPACE_UPDATE), rolePermission(Permission.ACCOUNT_READ)));

    service.revokePermissions(role, requested);

    verify(rolePermissionWriteRepository).deleteByRoleIdAndPermissions(
        ROLE_ID, Set.of(Permission.WORKSPACE_UPDATE, Permission.ACCOUNT_READ));
  }

  @Test
  void shouldNotRevokeMultiplePermissionsWhenNoneExist() {
    Role role = activeWorkspaceRole();
    Set<Permission> requested = Set.of(Permission.WORKSPACE_UPDATE);
    when(rolePermissionReadRepository.findByRoleIdAndPermissionsIn(ROLE_ID, requested))
        .thenReturn(List.of());

    service.revokePermissions(role, requested);

    verify(rolePermissionWriteRepository, never()).deleteByRoleIdAndPermissions(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anySet());
  }

  @Test
  void shouldThrowWhenRoleIsDeleted() {
    Role role = deletedWorkspaceRole();

    assertThatThrownBy(() -> service.grantPermission(role, Permission.ACCOUNT_READ, Instant.now()))
        .isInstanceOf(DomainRuleViolationException.class)
        .hasMessage("Role is already deleted.");
  }
}
