package com.ekapasha.auth_service.role.presentation.dto.role;

import org.junit.jupiter.api.Test;

import static com.ekapasha.auth_service.role.RoleTestFixtures.OWNER_ID;
import static com.ekapasha.auth_service.role.RoleTestFixtures.WORKSPACE_ID;
import static org.assertj.core.api.Assertions.assertThat;

class CreateRoleRequestDtoTest {

  @Test
  void shouldMapToCreateRoleCommand() {
    CreateRoleRequestDto dto = new CreateRoleRequestDto(WORKSPACE_ID, "Role", "Description");

    var command = dto.toCreateRoleCommand(OWNER_ID);

    assertThat(command.workspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(command.name()).isEqualTo("Role");
    assertThat(command.description()).isEqualTo("Description");
    assertThat(command.invokedBy()).isEqualTo(OWNER_ID);
  }
}
