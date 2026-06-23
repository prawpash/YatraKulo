package com.ekapasha.auth_service.role;

import com.ekapasha.auth_service.role.application.command.role.CreateRoleCommand;
import com.ekapasha.auth_service.role.application.command.role.UpdateRoleCommand;
import com.ekapasha.auth_service.role.application.command.role.UpdateRolePermissionsCommand;
import com.ekapasha.auth_service.role.application.query.permission.GetPermissionByCodeQuery;
import com.ekapasha.auth_service.role.application.query.permission.ListPermissionsQuery;
import com.ekapasha.auth_service.role.application.query.role.GetRoleByIdQuery;
import com.ekapasha.auth_service.role.application.query.role.ListRolesQuery;
import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.presentation.dto.permission.ListPermissionsFilterDto;
import com.ekapasha.auth_service.role.presentation.dto.permission.PermissionResponseDto;
import com.ekapasha.auth_service.role.presentation.dto.role.CreateRoleRequestDto;
import com.ekapasha.auth_service.role.presentation.dto.role.ListRolesFilterDto;
import com.ekapasha.auth_service.role.presentation.dto.role.UpdateRolePermissionsRequestDto;
import com.ekapasha.auth_service.role.presentation.dto.role.UpdateRoleRequestDto;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.ekapasha.auth_service.role.RoleTestFixtures.ROLE_ID;
import static com.ekapasha.auth_service.role.RoleTestFixtures.WORKSPACE_ID;
import static org.assertj.core.api.Assertions.assertThat;

class RoleModelTest {

  @Test
  void shouldExposeRecordFieldsForCommandsAndQueries() {
    CreateRoleCommand createCommand =
        new CreateRoleCommand(WORKSPACE_ID, "Admin", "Admin role", ROLE_ID);
    UpdateRoleCommand updateCommand = new UpdateRoleCommand(ROLE_ID, "Admin", "Admin role", ROLE_ID);
    UpdateRolePermissionsCommand permissionsCommand =
        new UpdateRolePermissionsCommand(
            ROLE_ID, Set.of(Permission.WORKSPACE_UPDATE), Set.of(Permission.WORKSPACE_DELETE), ROLE_ID);

    GetPermissionByCodeQuery permissionQuery = new GetPermissionByCodeQuery("workspace.update");
    ListPermissionsQuery listPermissionsQuery = new ListPermissionsQuery("admin", new com.ekapasha.shared.pagination.DomainPageRequest(1, 20));
    GetRoleByIdQuery getRoleByIdQuery = new GetRoleByIdQuery(ROLE_ID);
    ListRolesQuery listRolesQuery = new ListRolesQuery(WORKSPACE_ID, true, "admin", new com.ekapasha.shared.pagination.DomainPageRequest(2, 15), false);

    assertThat(createCommand.workspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(createCommand.name()).isEqualTo("Admin");
    assertThat(createCommand.description()).isEqualTo("Admin role");
    assertThat(createCommand.invokedBy()).isEqualTo(ROLE_ID);

    assertThat(updateCommand.id()).isEqualTo(ROLE_ID);
    assertThat(updateCommand.name()).isEqualTo("Admin");
    assertThat(updateCommand.description()).isEqualTo("Admin role");
    assertThat(updateCommand.invokedBy()).isEqualTo(ROLE_ID);

    assertThat(permissionsCommand.roleId()).isEqualTo(ROLE_ID);
    assertThat(permissionsCommand.assign()).containsExactly(Permission.WORKSPACE_UPDATE);
    assertThat(permissionsCommand.revoke()).containsExactly(Permission.WORKSPACE_DELETE);
    assertThat(permissionsCommand.invokedBy()).isEqualTo(ROLE_ID);

    assertThat(permissionQuery.code()).isEqualTo("workspace.update");
    assertThat(listPermissionsQuery.search()).isEqualTo("admin");
    assertThat(listPermissionsQuery.pageRequest().page()).isEqualTo(1);
    assertThat(getRoleByIdQuery.id()).isEqualTo(ROLE_ID);
    assertThat(listRolesQuery.workspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(listRolesQuery.includeGlobal()).isTrue();
    assertThat(listRolesQuery.searchTerm()).isEqualTo("admin");
    assertThat(listRolesQuery.pageRequest().size()).isEqualTo(15);
    assertThat(listRolesQuery.includeDeleted()).isFalse();
  }

  @Test
  void shouldMapRoleFilterDtosToQueries() {
    ListPermissionsFilterDto permissionsFilter = new ListPermissionsFilterDto(0, 25, "read");
    ListRolesFilterDto rolesFilter = new ListRolesFilterDto(WORKSPACE_ID, 2, 50, true, false, "admin");

    assertThat(permissionsFilter.toListPermissionsQuery().search()).isEqualTo("read");
    assertThat(permissionsFilter.toListPermissionsQuery().pageRequest().page()).isEqualTo(0);
    assertThat(permissionsFilter.toListPermissionsQuery().pageRequest().size()).isEqualTo(25);

    assertThat(rolesFilter.includeDeletedOrNot()).isTrue();
    assertThat(rolesFilter.includeGlobalOrNot()).isFalse();
    assertThat(rolesFilter.toListRolesQuery().workspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(rolesFilter.toListRolesQuery().includeDeleted()).isTrue();
    assertThat(rolesFilter.toListRolesQuery().includeGlobal()).isFalse();
  }

  @Test
  void shouldMapRoleRequestDtosToCommands() {
    CreateRoleRequestDto createDto = new CreateRoleRequestDto(WORKSPACE_ID, "Admin", "Admin role");
    UpdateRoleRequestDto updateDto = new UpdateRoleRequestDto("Admin", "Admin role");
    UpdateRolePermissionsRequestDto permissionsDto =
        new UpdateRolePermissionsRequestDto(
            Set.of("workspace.update", "workspace.delete"), Set.of("account.read"));

    assertThat(createDto.toCreateRoleCommand(ROLE_ID).workspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(createDto.toCreateRoleCommand(ROLE_ID).invokedBy()).isEqualTo(ROLE_ID);

    assertThat(updateDto.toUpdateRoleCommand(ROLE_ID, ROLE_ID).id()).isEqualTo(ROLE_ID);
    assertThat(updateDto.toUpdateRoleCommand(ROLE_ID, ROLE_ID).name()).isEqualTo("Admin");

    assertThat(permissionsDto.toCommand(ROLE_ID, ROLE_ID).assign())
        .containsExactlyInAnyOrder(Permission.WORKSPACE_UPDATE, Permission.WORKSPACE_DELETE);
    assertThat(permissionsDto.toCommand(ROLE_ID, ROLE_ID).revoke())
        .containsExactly(Permission.ACCOUNT_READ);
  }

  @Test
  void shouldMapPermissionToResponseDto() {
    PermissionResponseDto dto = PermissionResponseDto.from(Permission.WORKSPACE_INVITE);

    assertThat(dto.code()).isEqualTo("workspace.invite");
    assertThat(dto.description()).isEqualTo("Invite user to workspace");
  }
}
