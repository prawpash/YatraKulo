package com.ekapasha.auth_service.role.presentation.dto.role;

import org.junit.jupiter.api.Test;

import static com.ekapasha.auth_service.role.RoleTestFixtures.OTHER_USER_ID;
import static com.ekapasha.auth_service.role.RoleTestFixtures.ROLE_ID;
import static org.assertj.core.api.Assertions.assertThat;

class UpdateRoleRequestDtoTest {

  @Test
  void shouldMapToUpdateRoleCommand() {
    UpdateRoleRequestDto dto = new UpdateRoleRequestDto("New name", "New description");

    var command = dto.toUpdateRoleCommand(ROLE_ID, OTHER_USER_ID);

    assertThat(command.id()).isEqualTo(ROLE_ID);
    assertThat(command.name()).isEqualTo("New name");
    assertThat(command.description()).isEqualTo("New description");
    assertThat(command.invokedBy()).isEqualTo(OTHER_USER_ID);
  }
}
