package com.ekapasha.auth_service.role.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

class RoleEntityTest {

  @Test
  void shouldUseIdBasedEquality() {
    RoleEntity left =
        new RoleEntity(ROLE_ID, WORKSPACE_ID, "Admin", "Role A", CREATED_AT, UPDATED_AT, null, CREATED_BY, UPDATED_BY, null);
    RoleEntity right =
        new RoleEntity(ROLE_ID, null, "Other", "Role B", CREATED_AT, UPDATED_AT, DELETED_AT, null, null, null);

    assertThat(left).isEqualTo(right);
    assertThat(left).hasSameHashCodeAs(right);
  }
}
