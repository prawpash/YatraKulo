package com.ekapasha.auth_service.role.presentation.dto.permission;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionResponseDtoTest {

  @Test
  void shouldMapFromPermission() {
    PermissionResponseDto dto = PermissionResponseDto.from(Permission.ACCOUNT_CREATE);

    assertThat(dto.code()).isEqualTo("account.create");
    assertThat(dto.description()).isEqualTo("Create account");
  }
}
