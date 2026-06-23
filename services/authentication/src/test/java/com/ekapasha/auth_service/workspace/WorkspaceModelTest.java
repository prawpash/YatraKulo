package com.ekapasha.auth_service.workspace;

import com.ekapasha.auth_service.workspace.application.command.workspace.CreateWorkspaceCommand;
import com.ekapasha.auth_service.workspace.application.command.workspace.DeleteWorkspaceCommand;
import com.ekapasha.auth_service.workspace.application.command.workspace.SetWorkspaceDefaultCommand;
import com.ekapasha.auth_service.workspace.application.command.workspace.UpdateWorkspaceCommand;
import com.ekapasha.auth_service.workspace.application.command.workspacemember.AddWorkspaceMemberCommand;
import com.ekapasha.auth_service.workspace.application.command.workspacemember.RemoveWorkspaceMemberCommand;
import com.ekapasha.auth_service.workspace.application.command.workspacemember.UpdateWorkspaceMemberRoleCommand;
import com.ekapasha.auth_service.workspace.application.query.workspace.CheckWorkspaceExistsQuery;
import com.ekapasha.auth_service.workspace.application.query.workspace.CountWorkspacesQuery;
import com.ekapasha.auth_service.workspace.application.query.workspace.GetDefaultWorkspaceByOwnerQuery;
import com.ekapasha.auth_service.workspace.application.query.workspace.GetWorkspaceByIdAndOwnerIdQuery;
import com.ekapasha.auth_service.workspace.application.query.workspace.GetWorkspaceByIdQuery;
import com.ekapasha.auth_service.workspace.application.query.workspace.ListWorkspacesByOwnerQuery;
import com.ekapasha.auth_service.workspace.application.query.workspacemember.CheckWorkspaceMemberExistsQuery;
import com.ekapasha.auth_service.workspace.application.query.workspacemember.CountWorkspaceMembersQuery;
import com.ekapasha.auth_service.workspace.application.query.workspacemember.GetWorkspaceMemberByIdQuery;
import com.ekapasha.auth_service.workspace.application.query.workspacemember.GetWorkspaceMemberByWorkspaceAndUserQuery;
import com.ekapasha.auth_service.workspace.application.query.workspacemember.ListUserWorkspaceMembershipsQuery;
import com.ekapasha.auth_service.workspace.application.query.workspacemember.ListWorkspaceMembersByWorkspaceQuery;
import com.ekapasha.auth_service.workspace.application.query.workspacepermission.ListWorkspacePermissionsQuery;
import com.ekapasha.auth_service.workspace.presentation.dto.AddWorkspaceMemberRequestDto;
import com.ekapasha.auth_service.workspace.presentation.dto.CreateWorkspaceRequestDto;
import com.ekapasha.auth_service.workspace.presentation.dto.ListWorkspaceMembersFilterDto;
import com.ekapasha.auth_service.workspace.presentation.dto.ListWorkspacesFilterDto;
import com.ekapasha.auth_service.workspace.presentation.dto.SetDefaultRequestDto;
import com.ekapasha.auth_service.workspace.presentation.dto.UpdateWorkspaceMemberRoleRequestDto;
import com.ekapasha.auth_service.workspace.presentation.dto.UpdateWorkspaceRequestDto;
import org.junit.jupiter.api.Test;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.MEMBER_ID;
import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.OWNER_ID;
import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.ROLE_ID;
import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.WORKSPACE_ID;
import static org.assertj.core.api.Assertions.assertThat;

class WorkspaceModelTest {

  @Test
  void shouldExposeRecordFieldsForCommandsAndQueries() {
    CreateWorkspaceCommand createCommand =
        new CreateWorkspaceCommand("Workspace", "Description", OWNER_ID, true, OWNER_ID);
    DeleteWorkspaceCommand deleteCommand = new DeleteWorkspaceCommand(WORKSPACE_ID, OWNER_ID);
    SetWorkspaceDefaultCommand defaultCommand = new SetWorkspaceDefaultCommand(WORKSPACE_ID, true, OWNER_ID);
    UpdateWorkspaceCommand updateCommand = new UpdateWorkspaceCommand(WORKSPACE_ID, "Workspace", "Description", OWNER_ID);
    AddWorkspaceMemberCommand addMemberCommand = new AddWorkspaceMemberCommand(WORKSPACE_ID, OWNER_ID, ROLE_ID, OWNER_ID);
    RemoveWorkspaceMemberCommand removeMemberCommand = new RemoveWorkspaceMemberCommand(WORKSPACE_ID, OWNER_ID, OWNER_ID);
    UpdateWorkspaceMemberRoleCommand updateMemberRoleCommand =
        new UpdateWorkspaceMemberRoleCommand(WORKSPACE_ID, OWNER_ID, ROLE_ID, OWNER_ID);
    CheckWorkspaceExistsQuery checkWorkspaceExistsQuery = new CheckWorkspaceExistsQuery(WORKSPACE_ID, OWNER_ID);
    CountWorkspacesQuery countWorkspacesQuery = new CountWorkspacesQuery();
    GetDefaultWorkspaceByOwnerQuery defaultWorkspaceQuery = new GetDefaultWorkspaceByOwnerQuery(OWNER_ID);
    GetWorkspaceByIdAndOwnerIdQuery byIdAndOwnerQuery = new GetWorkspaceByIdAndOwnerIdQuery(WORKSPACE_ID, OWNER_ID);
    GetWorkspaceByIdQuery byIdQuery = new GetWorkspaceByIdQuery(WORKSPACE_ID);
    ListWorkspacesByOwnerQuery listWorkspacesQuery =
        new ListWorkspacesByOwnerQuery(OWNER_ID, new com.ekapasha.shared.pagination.DomainPageRequest(1, 10), "search");
    CheckWorkspaceMemberExistsQuery checkMemberExistsQuery = new CheckWorkspaceMemberExistsQuery(WORKSPACE_ID, OWNER_ID);
    CountWorkspaceMembersQuery countWorkspaceMembersQuery = new CountWorkspaceMembersQuery(WORKSPACE_ID);
    GetWorkspaceMemberByIdQuery getWorkspaceMemberByIdQuery = new GetWorkspaceMemberByIdQuery(MEMBER_ID);
    GetWorkspaceMemberByWorkspaceAndUserQuery byWorkspaceAndUserQuery =
        new GetWorkspaceMemberByWorkspaceAndUserQuery(WORKSPACE_ID, OWNER_ID);
    ListUserWorkspaceMembershipsQuery membershipsQuery =
        new ListUserWorkspaceMembershipsQuery(OWNER_ID, new com.ekapasha.shared.pagination.DomainPageRequest(2, 5));
    ListWorkspaceMembersByWorkspaceQuery membersByWorkspaceQuery =
        new ListWorkspaceMembersByWorkspaceQuery(WORKSPACE_ID, new com.ekapasha.shared.pagination.DomainPageRequest(3, 15), "jane");
    ListWorkspacePermissionsQuery permissionsQuery =
        new ListWorkspacePermissionsQuery(WORKSPACE_ID, OWNER_ID, OWNER_ID);

    assertThat(createCommand.name()).isEqualTo("Workspace");
    assertThat(createCommand.description()).isEqualTo("Description");
    assertThat(createCommand.ownerId()).isEqualTo(OWNER_ID);
    assertThat(createCommand.isDefault()).isTrue();

    assertThat(deleteCommand.id()).isEqualTo(WORKSPACE_ID);
    assertThat(defaultCommand.isDefault()).isTrue();
    assertThat(updateCommand.name()).isEqualTo("Workspace");
    assertThat(addMemberCommand.roleId()).isEqualTo(ROLE_ID);
    assertThat(removeMemberCommand.userId()).isEqualTo(OWNER_ID);
    assertThat(updateMemberRoleCommand.invokedBy()).isEqualTo(OWNER_ID);

    assertThat(checkWorkspaceExistsQuery.id()).isEqualTo(WORKSPACE_ID);
    assertThat(countWorkspacesQuery).isNotNull();
    assertThat(defaultWorkspaceQuery.ownerId()).isEqualTo(OWNER_ID);
    assertThat(byIdAndOwnerQuery.ownerId()).isEqualTo(OWNER_ID);
    assertThat(byIdQuery.id()).isEqualTo(WORKSPACE_ID);
    assertThat(listWorkspacesQuery.search()).isEqualTo("search");
    assertThat(checkMemberExistsQuery.workspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(countWorkspaceMembersQuery.workspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(getWorkspaceMemberByIdQuery.id()).isEqualTo(MEMBER_ID);
    assertThat(byWorkspaceAndUserQuery.userId()).isEqualTo(OWNER_ID);
    assertThat(membershipsQuery.pageRequest().size()).isEqualTo(5);
    assertThat(membersByWorkspaceQuery.pageRequest().page()).isEqualTo(3);
    assertThat(permissionsQuery.invokedBy()).isEqualTo(OWNER_ID);
  }

  @Test
  void shouldMapWorkspaceRequestDtosToCommandsAndPageRequests() {
    CreateWorkspaceRequestDto createDto =
        new CreateWorkspaceRequestDto("Workspace", "Description", true);
    UpdateWorkspaceRequestDto updateDto = new UpdateWorkspaceRequestDto("Workspace", "Description");
    AddWorkspaceMemberRequestDto addMemberDto = new AddWorkspaceMemberRequestDto(OWNER_ID, ROLE_ID);
    UpdateWorkspaceMemberRoleRequestDto updateMemberRoleDto = new UpdateWorkspaceMemberRoleRequestDto(ROLE_ID);
    SetDefaultRequestDto setDefaultDto = new SetDefaultRequestDto(true);
    ListWorkspacesFilterDto workspacesFilter = new ListWorkspacesFilterDto(0, 20, "search");
    ListWorkspaceMembersFilterDto membersFilter = new ListWorkspaceMembersFilterDto(1, 15, "jane");

    assertThat(createDto.toCommand(OWNER_ID, OWNER_ID).ownerId()).isEqualTo(OWNER_ID);
    assertThat(updateDto.toCommand(WORKSPACE_ID, OWNER_ID).id()).isEqualTo(WORKSPACE_ID);
    assertThat(addMemberDto.toCommand(WORKSPACE_ID, OWNER_ID).workspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(updateMemberRoleDto.toCommand(WORKSPACE_ID, OWNER_ID, OWNER_ID).roleId()).isEqualTo(ROLE_ID);
    assertThat(setDefaultDto.toCommand(WORKSPACE_ID, OWNER_ID).isDefault()).isTrue();

    assertThat(workspacesFilter.toPageRequest().page()).isEqualTo(0);
    assertThat(workspacesFilter.toPageRequest().size()).isEqualTo(20);
    assertThat(membersFilter.toPageRequest().page()).isEqualTo(1);
    assertThat(membersFilter.toPageRequest().size()).isEqualTo(15);
  }
}
