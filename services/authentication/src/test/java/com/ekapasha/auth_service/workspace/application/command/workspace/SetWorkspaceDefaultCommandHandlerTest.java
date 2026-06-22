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

import java.util.Optional;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SetWorkspaceDefaultCommandHandlerTest {

  @Mock private WorkspaceWriteRepository workspaceWriteRepository;
  @Mock private WorkspaceReadRepository workspaceReadRepository;
  @InjectMocks private SetWorkspaceDefaultCommandHandler handler;

  @Test
  void shouldSetWorkspaceAsDefaultAndUnsetCurrentDefault() {
    Workspace currentDefault = defaultWorkspace();
    Workspace target = workspace();
    when(workspaceReadRepository.findByIdAndOwnerId(WORKSPACE_ID, OWNER_ID))
        .thenReturn(Optional.of(target));
    when(workspaceReadRepository.findDefaultByOwnerId(OWNER_ID)).thenReturn(Optional.of(currentDefault));

    handler.handler(new SetWorkspaceDefaultCommand(WORKSPACE_ID, true, OWNER_ID));

    verify(workspaceWriteRepository).save(currentDefault);
    verify(workspaceWriteRepository).save(target);
    assertThat(currentDefault.isDefault()).isFalse();
    assertThat(target.isDefault()).isTrue();
  }

  @Test
  void shouldUnsetWorkspaceAsDefault() {
    Workspace target = defaultWorkspace();
    when(workspaceReadRepository.findByIdAndOwnerId(WORKSPACE_ID_2, OWNER_ID))
        .thenReturn(Optional.of(target));

    handler.handler(new SetWorkspaceDefaultCommand(WORKSPACE_ID_2, false, OWNER_ID));

    verify(workspaceWriteRepository).save(target);
    assertThat(target.isDefault()).isFalse();
  }

  @Test
  void shouldThrowWhenWorkspaceMissingOrUnauthorized() {
    when(workspaceReadRepository.findByIdAndOwnerId(WORKSPACE_ID, OWNER_ID))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> handler.handler(new SetWorkspaceDefaultCommand(WORKSPACE_ID, true, OWNER_ID)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Workspace not found.");
  }
}
