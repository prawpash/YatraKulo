package com.ekapasha.auth_service.workspace.application.command.workspacemember;

import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.service.WorkspaceMemberService;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.exception.UnauthorizedAccessException;
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
class RemoveWorkspaceMemberCommandHandlerTest {

  @Mock private WorkspaceMemberService workspaceMemberService;
  @Mock private WorkspaceReadRepository workspaceReadRepository;
  @InjectMocks private RemoveWorkspaceMemberCommandHandler handler;

  @Test
  void shouldAllowOwnerRemoval() {
    Workspace workspace = workspace();
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.of(workspace));

    handler.handler(new RemoveWorkspaceMemberCommand(WORKSPACE_ID, OTHER_USER_ID, OWNER_ID));

    verify(workspaceMemberService).removeMember(WORKSPACE_ID, OTHER_USER_ID);
  }

  @Test
  void shouldAllowSelfRemoval() {
    Workspace workspace = workspace();
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.of(workspace));

    handler.handler(new RemoveWorkspaceMemberCommand(WORKSPACE_ID, OTHER_USER_ID, OTHER_USER_ID));

    verify(workspaceMemberService).removeMember(WORKSPACE_ID, OTHER_USER_ID);
  }

  @Test
  void shouldThrowWhenWorkspaceMissing() {
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> handler.handler(new RemoveWorkspaceMemberCommand(WORKSPACE_ID, OTHER_USER_ID, OWNER_ID)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Workspace not found.");
  }

  @Test
  void shouldThrowWhenCallerIsNotAllowed() {
    Workspace workspace = workspace();
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.of(workspace));

    assertThatThrownBy(() -> handler.handler(new RemoveWorkspaceMemberCommand(WORKSPACE_ID, OTHER_USER_ID, UPDATED_BY)))
        .isInstanceOf(UnauthorizedAccessException.class)
        .hasMessage("Only the workspace owner can remove members, or members can leave on their own.");
  }
}
