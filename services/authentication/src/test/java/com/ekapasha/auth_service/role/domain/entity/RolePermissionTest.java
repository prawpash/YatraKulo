package com.ekapasha.auth_service.role.domain.entity;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import org.junit.jupiter.api.Test;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RolePermissionTest {

  @Test
  void shouldBuildRolePermission() {
    RolePermission rolePermission = rolePermission(Permission.WORKSPACE_DELETE);

    assertThat(rolePermission.getId()).isEqualTo(ROLE_PERMISSION_ID);
    assertThat(rolePermission.getRoleId()).isEqualTo(ROLE_ID);
    assertThat(rolePermission.getPermission()).isEqualTo(Permission.WORKSPACE_DELETE);
    assertThat(rolePermission.getAddedAt()).isEqualTo(CREATED_AT);
  }

  @Test
  void shouldRejectNullRequiredFields() {
    assertThatThrownBy(
            () -> RolePermission.builder()
                .id(null)
                .roleId(ROLE_ID)
                .permission(Permission.WORKSPACE_DELETE)
                .addedAt(CREATED_AT)
                .build())
        .isInstanceOf(NullPointerException.class);
  }
}
