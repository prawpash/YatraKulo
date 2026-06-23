package com.ekapasha.auth_service.workspace.application.command.workspace;

import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceWriteRepository;
import com.ekapasha.shared.exception.DomainRuleViolationException;
import com.ekapasha.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteWorkspaceCommandHandlerTest {

  @Mock private WorkspaceWriteRepository workspaceWriteRepository;
  @Mock private WorkspaceReadRepository workspaceReadRepository;
  @InjectMocks private DeleteWorkspaceCommandHandler handler;

  @Test
  void shouldDeleteWorkspaceWhenAuthorized() {
    Workspace workspace = workspace();
    when(workspaceReadRepository.findByIdAndOwnerId(WORKSPACE_ID, OWNER_ID))
        .thenReturn(java.util.Optional.of(workspace));

    handler.handler(new DeleteWorkspaceCommand(WORKSPACE_ID, OWNER_ID));

    verify(workspaceWriteRepository).deleteById(WORKSPACE_ID);
  }

  @Test
  void shouldRejectDefaultWorkspaceDeletion() {
    Workspace workspace = defaultWorkspace();
    when(workspaceReadRepository.findByIdAndOwnerId(WORKSPACE_ID_2, OWNER_ID))
        .thenReturn(java.util.Optional.of(workspace));

    assertThatThrownBy(() -> handler.handler(new DeleteWorkspaceCommand(WORKSPACE_ID_2, OWNER_ID)))
        .isInstanceOf(DomainRuleViolationException.class)
        .hasMessage("Cannot delete the default workspace. Set another workspace as default first.");
  }

  @Test
  void shouldThrowWhenWorkspaceMissingOrUnauthorized() {
    when(workspaceReadRepository.findByIdAndOwnerId(WORKSPACE_ID, OWNER_ID))
        .thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> handler.handler(new DeleteWorkspaceCommand(WORKSPACE_ID, OWNER_ID)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Workspace not found.");
  }
}
