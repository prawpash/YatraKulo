package com.ekapasha.auth_service.role.domain.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PermissionTest {

  @Test
  void shouldExposeCodeAndDescriptionAndResolveByCode() {
    assertThat(Permission.WORKSPACE_UPDATE.getCode()).isEqualTo("workspace.update");
    assertThat(Permission.WORKSPACE_UPDATE.getDescription()).isEqualTo("Update workspace");
    assertThat(Permission.fromCode("workspace.update")).isEqualTo(Permission.WORKSPACE_UPDATE);
  }

  @Test
  void shouldRejectUnknownPermissionCode() {
    assertThatThrownBy(() -> Permission.fromCode("unknown"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid permission code: unknown");
  }
}
