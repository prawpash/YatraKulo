package com.ekapasha.auth_service.workspace.presentation.dto;

import org.junit.jupiter.api.Test;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.OWNER_ID;
import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.OTHER_USER_ID;
import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.ROLE_ID;
import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.WORKSPACE_ID;
import static org.assertj.core.api.Assertions.assertThat;

class UpdateWorkspaceMemberRoleRequestDtoTest {

  @Test
  void shouldMapToCommand() {
    UpdateWorkspaceMemberRoleRequestDto dto = new UpdateWorkspaceMemberRoleRequestDto(ROLE_ID);

    var command = dto.toCommand(WORKSPACE_ID, OTHER_USER_ID, OWNER_ID);

    assertThat(command.workspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(command.userId()).isEqualTo(OTHER_USER_ID);
    assertThat(command.roleId()).isEqualTo(ROLE_ID);
    assertThat(command.invokedBy()).isEqualTo(OWNER_ID);
  }
}
