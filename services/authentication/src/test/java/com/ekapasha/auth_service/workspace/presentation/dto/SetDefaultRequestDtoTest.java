package com.ekapasha.auth_service.workspace.presentation.dto;

import org.junit.jupiter.api.Test;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.OWNER_ID;
import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.WORKSPACE_ID;
import static org.assertj.core.api.Assertions.assertThat;

class SetDefaultRequestDtoTest {

  @Test
  void shouldMapToCommand() {
    SetDefaultRequestDto dto = new SetDefaultRequestDto(true);

    var command = dto.toCommand(WORKSPACE_ID, OWNER_ID);

    assertThat(command.id()).isEqualTo(WORKSPACE_ID);
    assertThat(command.isDefault()).isTrue();
    assertThat(command.invokedBy()).isEqualTo(OWNER_ID);
  }
}
