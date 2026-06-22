package com.ekapasha.auth_service.role.application.command.role;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.repository.RoleWriteRepository;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.exception.UnauthorizedAccessException;
import com.ekapasha.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRoleCommandHandlerTest {

  @Mock private RoleWriteRepository roleWriteRepository;
  @Mock private WorkspaceReadRepository workspaceReadRepository;
  @InjectMocks private CreateRoleCommandHandler handler;

  @Test
  void shouldCreateRoleWhenWorkspaceExistsAndCallerIsOwner() {
    Workspace workspace = workspace();
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.of(workspace));
    when(roleWriteRepository.save(org.mockito.ArgumentMatchers.any(Role.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Role result =
        handler.handler(new CreateRoleCommand(WORKSPACE_ID, "Role name", "Role description", OWNER_ID));

    ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
    verify(roleWriteRepository).save(captor.capture());
    Role saved = captor.getValue();
    assertThat(result).isEqualTo(saved);
    assertThat(saved.getWorkspaceId()).contains(WORKSPACE_ID);
    assertThat(saved.getName()).isEqualTo("Role name");
    assertThat(saved.getDescription()).isEqualTo("Role description");
    assertThat(saved.getCreatedBy()).contains(OWNER_ID);
    assertThat(saved.getUpdatedBy()).contains(OWNER_ID);
    assertThat(saved.getCreatedAt()).isEqualTo(saved.getUpdatedAt());
  }

  @Test
  void shouldThrowWhenWorkspaceMissing() {
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(
            () ->
                handler.handler(
                    new CreateRoleCommand(WORKSPACE_ID, "Role name", "Role description", OWNER_ID)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Workspace not found.");
  }

  @Test
  void shouldThrowWhenCallerIsNotWorkspaceOwner() {
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.of(workspace()));

    assertThatThrownBy(
            () ->
                handler.handler(
                    new CreateRoleCommand(
                        WORKSPACE_ID, "Role name", "Role description", OTHER_USER_ID)))
        .isInstanceOf(UnauthorizedAccessException.class)
        .hasMessage("Only workspace owner can create roles.");
  }

  @Test
  void shouldThrowWhenNameIsBlank() {
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.of(workspace()));

    assertThatThrownBy(
            () -> handler.handler(new CreateRoleCommand(WORKSPACE_ID, " ", "Role description", OWNER_ID)))
        .isInstanceOf(ValidationException.class)
        .satisfies(
            throwable -> {
              ValidationException exception = (ValidationException) throwable;
              assertThat(exception.getProperty()).isEqualTo("name");
              assertThat(exception).hasMessage("name must not be null or blank.");
            });
  }
}
