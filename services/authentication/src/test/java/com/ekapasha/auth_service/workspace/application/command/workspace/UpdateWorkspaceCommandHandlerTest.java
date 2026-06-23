package com.ekapasha.auth_service.workspace.application.command.workspace;

import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceWriteRepository;
import com.ekapasha.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateWorkspaceCommandHandlerTest {

  @Mock private WorkspaceWriteRepository workspaceWriteRepository;
  @Mock private WorkspaceReadRepository workspaceReadRepository;
  @InjectMocks private UpdateWorkspaceCommandHandler handler;

  @Test
  void shouldUpdateNameAndDescriptionWhenFoundAndAuthorized() {
    Workspace workspace = workspace();
    when(workspaceReadRepository.findByIdAndOwnerId(WORKSPACE_ID, OWNER_ID))
        .thenReturn(java.util.Optional.of(workspace));

    handler.handler(new UpdateWorkspaceCommand(WORKSPACE_ID, "New name", "New description", OWNER_ID));

    verify(workspaceWriteRepository).save(workspace);
    assertThat(workspace.getName()).isEqualTo("New name");
    assertThat(workspace.getDescription()).contains("New description");
  }

  @Test
  void shouldIgnoreBlankNameAndNullDescription() {
    Workspace workspace = workspace();
    when(workspaceReadRepository.findByIdAndOwnerId(WORKSPACE_ID, OWNER_ID))
        .thenReturn(java.util.Optional.of(workspace));

    handler.handler(new UpdateWorkspaceCommand(WORKSPACE_ID, " ", null, OWNER_ID));

    verify(workspaceWriteRepository).save(workspace);
    assertThat(workspace.getName()).isEqualTo("Workspace");
    assertThat(workspace.getDescription()).contains("Workspace description");
  }

  @Test
  void shouldThrowWhenWorkspaceMissingOrUnauthorized() {
    when(workspaceReadRepository.findByIdAndOwnerId(WORKSPACE_ID, OWNER_ID))
        .thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> handler.handler(new UpdateWorkspaceCommand(WORKSPACE_ID, "New name", null, OWNER_ID)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Workspace not found.");
  }
}
