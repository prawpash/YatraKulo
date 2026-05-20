package com.ekapasha.auth_service.role.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

class RolePermissionEntityTest {

  @Test
  void shouldUseIdBasedEquality() {
    RolePermissionEntity left = new RolePermissionEntity(ROLE_PERMISSION_ID, ROLE_ID, "workspace.update", CREATED_AT);
    RolePermissionEntity right = new RolePermissionEntity(ROLE_PERMISSION_ID, WORKSPACE_ID, "account.read", UPDATED_AT);

    assertThat(left).isEqualTo(right);
    assertThat(left).hasSameHashCodeAs(right);
  }
}
