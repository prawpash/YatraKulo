package com.ekapasha.auth_service.role.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionEntityTest {

  @Test
  void shouldUseCodeBasedEquality() {
    PermissionEntity left = new PermissionEntity("workspace.update", "Update workspace");
    PermissionEntity right = new PermissionEntity("workspace.update", "Different description");

    assertThat(left).isEqualTo(right);
    assertThat(left).hasSameHashCodeAs(right);
  }
}
