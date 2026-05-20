package com.ekapasha.auth_service.role.infrastructure.persistence.repository;

import com.ekapasha.auth_service.role.domain.entity.RolePermission;
import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.infrastructure.persistence.entity.RolePermissionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolePermissionReadRepositoryImplTest {

  @Mock private JPARolePermissionRepository jpaRepository;

  @Test
  void shouldDelegateAndMapAllReadOperations() {
    RolePermissionReadRepositoryImpl repository = new RolePermissionReadRepositoryImpl(jpaRepository);
    RolePermissionEntity entity =
        new RolePermissionEntity(ROLE_PERMISSION_ID, ROLE_ID, "workspace.update", CREATED_AT);
    when(jpaRepository.existsByRoleIdAndPermissionCode(ROLE_ID, "workspace.update")).thenReturn(true);
    when(jpaRepository.findById(ROLE_PERMISSION_ID)).thenReturn(Optional.of(entity));
    when(jpaRepository.findByRoleId(ROLE_ID)).thenReturn(List.of(entity));
    when(jpaRepository.findByRoleIdAndPermissionCodeIn(eq(ROLE_ID), anyList()))
        .thenReturn(List.of(entity));
    when(jpaRepository.countByRoleId(ROLE_ID)).thenReturn(7);

    assertThat(repository.exists(ROLE_ID, Permission.WORKSPACE_UPDATE)).isTrue();
    assertThat(repository.findById(ROLE_PERMISSION_ID)).hasValueSatisfying(rp -> assertThat(rp.getPermission()).isEqualTo(Permission.WORKSPACE_UPDATE));
    assertThat(repository.findByRoleId(ROLE_ID)).hasSize(1);
    assertThat(repository.findByRoleIdAndPermissionsIn(ROLE_ID, Set.of(Permission.WORKSPACE_UPDATE, Permission.ACCOUNT_READ)))
        .hasSize(1);
    assertThat(repository.countByRoleId(ROLE_ID)).isEqualTo(7);

    ArgumentCaptor<List<String>> codesCaptor = ArgumentCaptor.forClass(List.class);
    verify(jpaRepository).findByRoleIdAndPermissionCodeIn(eq(ROLE_ID), codesCaptor.capture());
    assertThat(codesCaptor.getValue()).containsExactlyInAnyOrder("workspace.update", "account.read");
  }
}
