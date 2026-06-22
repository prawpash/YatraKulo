package com.ekapasha.auth_service.workspace.presentation.controller;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.workspace.application.command.workspace.CreateWorkspaceCommand;
import com.ekapasha.auth_service.workspace.application.command.workspace.CreateWorkspaceCommandHandler;
import com.ekapasha.auth_service.workspace.application.command.workspace.DeleteWorkspaceCommand;
import com.ekapasha.auth_service.workspace.application.command.workspace.DeleteWorkspaceCommandHandler;
import com.ekapasha.auth_service.workspace.application.command.workspace.SetWorkspaceDefaultCommandHandler;
import com.ekapasha.auth_service.workspace.application.command.workspace.UpdateWorkspaceCommand;
import com.ekapasha.auth_service.workspace.application.command.workspace.UpdateWorkspaceCommandHandler;
import com.ekapasha.auth_service.workspace.application.command.workspacemember.AddWorkspaceMemberCommand;
import com.ekapasha.auth_service.workspace.application.command.workspacemember.AddWorkspaceMemberCommandHandler;
import com.ekapasha.auth_service.workspace.application.command.workspacemember.RemoveWorkspaceMemberCommand;
import com.ekapasha.auth_service.workspace.application.command.workspacemember.RemoveWorkspaceMemberCommandHandler;
import com.ekapasha.auth_service.workspace.application.command.workspacemember.UpdateWorkspaceMemberRoleCommand;
import com.ekapasha.auth_service.workspace.application.command.workspacemember.UpdateWorkspaceMemberRoleCommandHandler;
import com.ekapasha.auth_service.workspace.application.query.workspace.*;
import com.ekapasha.auth_service.workspace.application.query.workspacemember.*;
import com.ekapasha.auth_service.workspace.application.query.workspacepermission.ListWorkspacePermissionsQuery;
import com.ekapasha.auth_service.workspace.application.query.workspacepermission.ListWorkspacePermissionsQueryHandler;
import com.ekapasha.auth_service.workspace.presentation.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkspaceControllerTest {

  @Mock private ListWorkspacesByOwnerQueryHandler listWorkspacesByOwnerQueryHandler;
  @Mock private GetWorkspaceByIdQueryHandler getWorkspaceByIdQueryHandler;
  @Mock private GetDefaultWorkspaceByOwnerQueryHandler getDefaultWorkspaceByOwnerQueryHandler;
  @Mock private CreateWorkspaceCommandHandler createWorkspaceCommandHandler;
  @Mock private UpdateWorkspaceCommandHandler updateWorkspaceCommandHandler;
  @Mock private DeleteWorkspaceCommandHandler deleteWorkspaceCommandHandler;
  @Mock private SetWorkspaceDefaultCommandHandler setWorkspaceDefaultCommandHandler;
  @Mock private AddWorkspaceMemberCommandHandler addWorkspaceMemberCommandHandler;
  @Mock private RemoveWorkspaceMemberCommandHandler removeWorkspaceMemberCommandHandler;
  @Mock private UpdateWorkspaceMemberRoleCommandHandler updateWorkspaceMemberRoleCommandHandler;
  @Mock private ListWorkspaceMembersByWorkspaceQueryHandler listWorkspaceMembersByWorkspaceQueryHandler;
  @Mock private GetWorkspaceMemberByWorkspaceAndUserQueryHandler getWorkspaceMemberByWorkspaceAndUserQueryHandler;
  @Mock private ListWorkspacePermissionsQueryHandler listWorkspacePermissionsQueryHandler;
  @Mock private Jwt jwt;
  @InjectMocks private WorkspaceController controller;

  @Test
  void shouldCreateWorkspace() {
    when(jwt.getSubject()).thenReturn(OWNER_ID.toString());
    when(createWorkspaceCommandHandler.handler(any())).thenReturn(workspace());

    var response =
        controller.createWorkspace(
            new CreateWorkspaceRequestDto("Workspace", "Description", true), jwt);

    ArgumentCaptor<CreateWorkspaceCommand> captor = ArgumentCaptor.forClass(CreateWorkspaceCommand.class);
    verify(createWorkspaceCommandHandler).handler(captor.capture());
    assertThat(captor.getValue().ownerId()).isEqualTo(OWNER_ID);
    assertThat(response.getHeaders().getLocation())
        .isNotNull()
        .hasToString("/api/v1/workspaces/" + WORKSPACE_ID);
  }

  @Test
  void shouldListWorkspaces() {
    when(jwt.getSubject()).thenReturn(OWNER_ID.toString());
    when(listWorkspacesByOwnerQueryHandler.handler(any())).thenReturn(page(List.of(workspace())));

    var response = controller.listWorkspaces(new ListWorkspacesFilterDto(0, 10, "search"), jwt);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().content()).singleElement().satisfies(workspace -> assertThat(workspace.getName()).isEqualTo("Workspace"));
  }

  @Test
  void shouldGetWorkspace() {
    when(getWorkspaceByIdQueryHandler.handler(any())).thenReturn(workspace());

    var response = controller.getWorkspace(WORKSPACE_ID);

    assertThat(response.getBody()).isEqualTo(workspace());
  }

  @Test
  void shouldUpdateWorkspace() {
    when(jwt.getSubject()).thenReturn(OWNER_ID.toString());

    var response =
        controller.updateWorkspace(
            WORKSPACE_ID, new UpdateWorkspaceRequestDto("Workspace", "Description"), jwt);

    ArgumentCaptor<UpdateWorkspaceCommand> captor = ArgumentCaptor.forClass(UpdateWorkspaceCommand.class);
    verify(updateWorkspaceCommandHandler).handler(captor.capture());
    assertThat(captor.getValue().id()).isEqualTo(WORKSPACE_ID);
    assertThat(captor.getValue().invokedBy()).isEqualTo(OWNER_ID);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }

  @Test
  void shouldDeleteWorkspace() {
    when(jwt.getSubject()).thenReturn(OWNER_ID.toString());

    var response = controller.deleteWorkspace(WORKSPACE_ID, jwt);

    ArgumentCaptor<DeleteWorkspaceCommand> captor = ArgumentCaptor.forClass(DeleteWorkspaceCommand.class);
    verify(deleteWorkspaceCommandHandler).handler(captor.capture());
    assertThat(captor.getValue().id()).isEqualTo(WORKSPACE_ID);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }

  @Test
  void shouldSetDefaultWorkspace() {
    when(jwt.getSubject()).thenReturn(OWNER_ID.toString());

    var response = controller.setDefault(WORKSPACE_ID, new SetDefaultRequestDto(true), jwt);

    verify(setWorkspaceDefaultCommandHandler).handler(any());
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }

  @Test
  void shouldGetDefaultWorkspace() {
    when(jwt.getSubject()).thenReturn(OWNER_ID.toString());
    when(getDefaultWorkspaceByOwnerQueryHandler.handler(any())).thenReturn(defaultWorkspace());

    var response = controller.getDefaultWorkspace(jwt);

    assertThat(response.getBody()).isEqualTo(defaultWorkspace());
  }

  @Test
  void shouldAddMember() {
    when(jwt.getSubject()).thenReturn(OWNER_ID.toString());

    var response = controller.addMember(WORKSPACE_ID, new AddWorkspaceMemberRequestDto(OTHER_USER_ID, ROLE_ID), jwt);

    ArgumentCaptor<AddWorkspaceMemberCommand> captor = ArgumentCaptor.forClass(AddWorkspaceMemberCommand.class);
    verify(addWorkspaceMemberCommandHandler).handler(captor.capture());
    assertThat(captor.getValue().workspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(response.getHeaders().getLocation())
        .isNotNull()
        .hasToString("/api/v1/workspaces/" + WORKSPACE_ID + "/members/" + OTHER_USER_ID);
  }

  @Test
  void shouldListMembers() {
    when(listWorkspaceMembersByWorkspaceQueryHandler.handler(any()))
        .thenReturn(page(List.of(workspaceMember())));

    var response = controller.listMembers(WORKSPACE_ID, new ListWorkspaceMembersFilterDto(0, 10, "jane"));

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().content()).singleElement().satisfies(member -> assertThat(member.getWorkspaceId()).isEqualTo(WORKSPACE_ID));
  }

  @Test
  void shouldGetMember() {
    when(getWorkspaceMemberByWorkspaceAndUserQueryHandler.handler(any())).thenReturn(workspaceMember());

    var response = controller.getMember(WORKSPACE_ID, OTHER_USER_ID);

    assertThat(response.getBody()).isEqualTo(workspaceMember());
  }

  @Test
  void shouldListPermissions() {
    when(jwt.getSubject()).thenReturn(OWNER_ID.toString());
    when(listWorkspacePermissionsQueryHandler.handler(any()))
        .thenReturn(List.of(Permission.WORKSPACE_UPDATE.getCode()));

    var response = controller.listPermissions(WORKSPACE_ID, OTHER_USER_ID, jwt);

    ArgumentCaptor<ListWorkspacePermissionsQuery> captor = ArgumentCaptor.forClass(ListWorkspacePermissionsQuery.class);
    verify(listWorkspacePermissionsQueryHandler).handler(captor.capture());
    assertThat(captor.getValue().invokedBy()).isEqualTo(OWNER_ID);
    assertThat(response.getBody()).containsExactly(Permission.WORKSPACE_UPDATE.getCode());
  }

  @Test
  void shouldUpdateMemberRole() {
    when(jwt.getSubject()).thenReturn(OWNER_ID.toString());

    var response =
        controller.updateMemberRole(
            WORKSPACE_ID, OTHER_USER_ID, new UpdateWorkspaceMemberRoleRequestDto(ROLE_ID), jwt);

    ArgumentCaptor<UpdateWorkspaceMemberRoleCommand> captor =
        ArgumentCaptor.forClass(UpdateWorkspaceMemberRoleCommand.class);
    verify(updateWorkspaceMemberRoleCommandHandler).handler(captor.capture());
    assertThat(captor.getValue().workspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }

  @Test
  void shouldRemoveMember() {
    when(jwt.getSubject()).thenReturn(OWNER_ID.toString());

    var response = controller.removeMember(WORKSPACE_ID, OTHER_USER_ID, jwt);

    ArgumentCaptor<RemoveWorkspaceMemberCommand> captor = ArgumentCaptor.forClass(RemoveWorkspaceMemberCommand.class);
    verify(removeWorkspaceMemberCommandHandler).handler(captor.capture());
    assertThat(captor.getValue().workspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }
}
