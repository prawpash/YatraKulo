package com.ekapasha.auth_service.workspace.presentation.dto;

import org.junit.jupiter.api.Test;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.OWNER_ID;
import static org.assertj.core.api.Assertions.assertThat;

class CreateWorkspaceRequestDtoTest {

  @Test
  void shouldMapToCommand() {
    CreateWorkspaceRequestDto dto = new CreateWorkspaceRequestDto("Workspace", "Description", true);

    var command = dto.toCommand(OWNER_ID, OWNER_ID);

    assertThat(command.name()).isEqualTo("Workspace");
    assertThat(command.description()).isEqualTo("Description");
    assertThat(command.ownerId()).isEqualTo(OWNER_ID);
    assertThat(command.invokedBy()).isEqualTo(OWNER_ID);
    assertThat(command.isDefault()).isTrue();
  }
}
