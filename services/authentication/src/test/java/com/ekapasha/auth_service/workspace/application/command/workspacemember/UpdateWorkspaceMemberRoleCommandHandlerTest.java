package com.ekapasha.auth_service.workspace.application.command.workspacemember;

import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.service.WorkspaceMemberService;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.shared.exception.DomainRuleViolationException;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateWorkspaceMemberRoleCommandHandlerTest {

  @Mock private WorkspaceMemberService workspaceMemberService;
  @Mock private WorkspaceReadRepository workspaceReadRepository;
  @InjectMocks private UpdateWorkspaceMemberRoleCommandHandler handler;

  @Test
  void shouldDelegateWhenAuthorized() {
    Workspace workspace = workspace();
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.of(workspace));

    handler.handler(new UpdateWorkspaceMemberRoleCommand(WORKSPACE_ID, OTHER_USER_ID, ROLE_ID_2, OWNER_ID));

    verify(workspaceMemberService).updateMemberRole(eq(WORKSPACE_ID), eq(OTHER_USER_ID), eq(ROLE_ID_2), any(), eq(OWNER_ID));
  }

  @Test
  void shouldThrowWhenWorkspaceMissing() {
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> handler.handler(new UpdateWorkspaceMemberRoleCommand(WORKSPACE_ID, OTHER_USER_ID, ROLE_ID_2, OWNER_ID)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Workspace not found.");
  }

  @Test
  void shouldThrowWhenCallerIsNotOwner() {
    Workspace workspace = workspace();
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.of(workspace));

    assertThatThrownBy(() -> handler.handler(new UpdateWorkspaceMemberRoleCommand(WORKSPACE_ID, OTHER_USER_ID, ROLE_ID_2, OTHER_USER_ID)))
        .isInstanceOf(UnauthorizedAccessException.class)
        .hasMessage("Only the workspace owner can change member roles.");
  }

  @Test
  void shouldRejectSelfRoleChangeByOwner() {
    Workspace workspace = workspace();
    when(workspaceReadRepository.findById(WORKSPACE_ID)).thenReturn(java.util.Optional.of(workspace));

    assertThatThrownBy(() -> handler.handler(new UpdateWorkspaceMemberRoleCommand(WORKSPACE_ID, OWNER_ID, ROLE_ID_2, OWNER_ID)))
        .isInstanceOf(DomainRuleViolationException.class)
        .hasMessage("Workspace owner cannot change their own role.");
  }
}
