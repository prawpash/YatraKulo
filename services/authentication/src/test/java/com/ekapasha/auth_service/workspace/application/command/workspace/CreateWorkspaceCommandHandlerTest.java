package com.ekapasha.auth_service.workspace.application.command.workspace;

import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceWriteRepository;
import com.ekapasha.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateWorkspaceCommandHandlerTest {

  @Mock private WorkspaceWriteRepository workspaceWriteRepository;
  @InjectMocks private CreateWorkspaceCommandHandler handler;

  @Test
  void shouldCreateWorkspaceAndSaveIt() {
    when(workspaceWriteRepository.save(any(Workspace.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Workspace result =
        handler.handler(new CreateWorkspaceCommand("Workspace", "Description", OWNER_ID, false, OWNER_ID));

    ArgumentCaptor<Workspace> captor = ArgumentCaptor.forClass(Workspace.class);
    verify(workspaceWriteRepository).save(captor.capture());
    Workspace saved = captor.getValue();
    assertThat(result).isSameAs(saved);
    assertThat(saved.getName()).isEqualTo("Workspace");
    assertThat(saved.getDescription()).contains("Description");
    assertThat(saved.getOwnerId()).isEqualTo(OWNER_ID);
    assertThat(saved.isDefault()).isFalse();
  }

  @Test
  void shouldCallSetDefaultWhenRequested() {
    when(workspaceWriteRepository.save(any(Workspace.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    handler.handler(new CreateWorkspaceCommand("Workspace", "Description", OWNER_ID, true, OWNER_ID));

    verify(workspaceWriteRepository).setDefault(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(OWNER_ID));
    verify(workspaceWriteRepository).save(any());
  }

  @Test
  void shouldRejectBlankName() {
    assertThatThrownBy(() -> handler.handler(new CreateWorkspaceCommand(" ", "Description", OWNER_ID, false, OWNER_ID)))
        .isInstanceOf(ValidationException.class)
        .hasMessage("name must not be null or blank.");
  }

  @Test
  void shouldRejectNullOwner() {
    assertThatThrownBy(() -> handler.handler(new CreateWorkspaceCommand("Workspace", "Description", null, false, OWNER_ID)))
        .isInstanceOf(ValidationException.class)
        .hasMessage("ownerId must not be null.");
  }
}
