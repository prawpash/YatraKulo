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
import java.util.Set;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RolePermissionWriteRepositoryImplTest {

  @Mock private JPARolePermissionRepository jpaRepository;

  @Test
  void shouldDelegateAllWriteOperationsAndMapEntities() {
    RolePermissionWriteRepositoryImpl repository = new RolePermissionWriteRepositoryImpl(jpaRepository);
    RolePermission rolePermission = rolePermission(Permission.WORKSPACE_UPDATE);

    repository.save(rolePermission);
    repository.deleteByRoleIdAndPermission(ROLE_ID, Permission.WORKSPACE_UPDATE);
    repository.saveAll(List.of(rolePermission));
    repository.deleteByRoleIdAndPermissions(ROLE_ID, Set.of(Permission.WORKSPACE_UPDATE, Permission.ACCOUNT_READ));

    ArgumentCaptor<RolePermissionEntity> singleCaptor = ArgumentCaptor.forClass(RolePermissionEntity.class);
    verify(jpaRepository).save(singleCaptor.capture());
    assertThat(singleCaptor.getValue().getPermissionCode()).isEqualTo("workspace.update");

    verify(jpaRepository).deleteByRoleIdAndPermissionCode(ROLE_ID, "workspace.update");
    ArgumentCaptor<List<RolePermissionEntity>> listCaptor = ArgumentCaptor.forClass(List.class);
    verify(jpaRepository).saveAll(listCaptor.capture());
    assertThat(listCaptor.getValue()).hasSize(1);
    assertThat(listCaptor.getValue().get(0).getPermissionCode()).isEqualTo("workspace.update");

    ArgumentCaptor<List<String>> codesCaptor = ArgumentCaptor.forClass(List.class);
    verify(jpaRepository).deleteByRoleIdAndPermissionCodeIn(eq(ROLE_ID), codesCaptor.capture());
    assertThat(codesCaptor.getValue()).containsExactlyInAnyOrder("workspace.update", "account.read");
  }
}
