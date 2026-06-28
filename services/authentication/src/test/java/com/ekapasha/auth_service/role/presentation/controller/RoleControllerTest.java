package com.ekapasha.auth_service.role.presentation.controller;

import com.ekapasha.auth_service.role.application.command.role.CreateRoleCommand;
import com.ekapasha.auth_service.role.application.command.role.UpdateRoleCommand;
import com.ekapasha.auth_service.role.application.command.role.UpdateRolePermissionsCommand;
import com.ekapasha.auth_service.role.application.command.role.CreateRoleCommandHandler;
import com.ekapasha.auth_service.role.application.command.role.UpdateRoleCommandHandler;
import com.ekapasha.auth_service.role.application.command.role.UpdateRolePermissionsCommandHandler;
import com.ekapasha.auth_service.role.application.query.role.GetRoleByIdQueryHandler;
import com.ekapasha.auth_service.role.application.query.role.ListRolesQueryHandler;
import com.ekapasha.auth_service.role.presentation.dto.role.CreateRoleRequestDto;
import com.ekapasha.auth_service.role.presentation.dto.role.ListRolesFilterDto;
import com.ekapasha.auth_service.role.presentation.dto.role.UpdateRolePermissionsRequestDto;
import com.ekapasha.auth_service.role.presentation.dto.role.UpdateRoleRequestDto;
import com.ekapasha.auth_service.role.presentation.dto.role.RoleResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

  @Mock private ListRolesQueryHandler listRolesQueryHandler;
  @Mock private GetRoleByIdQueryHandler getRoleByIdQueryHandler;
  @Mock private CreateRoleCommandHandler createRoleCommandHandler;
  @Mock private UpdateRoleCommandHandler updateRoleCommandHandler;
  @Mock private UpdateRolePermissionsCommandHandler updateRolePermissionsCommandHandler;
  @Mock private Jwt jwt;
  @InjectMocks private RoleController controller;

  @Test
  void shouldListRoles() {
    when(listRolesQueryHandler.handler(any())).thenReturn(page(List.of(activeWorkspaceRole())));

    var response = controller.listRoles(new ListRolesFilterDto(WORKSPACE_ID, 0, 10, false, false, "admin"));

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().content()).singleElement()
        .satisfies(role -> assertThat(role.name()).isEqualTo("Admin"));
  }

  @Test
  void shouldCreateRole() {
    when(jwt.getSubject()).thenReturn(OWNER_ID.toString());
    when(createRoleCommandHandler.handler(any())).thenReturn(activeWorkspaceRole());

    var response =
        controller.createRole(new CreateRoleRequestDto(WORKSPACE_ID, "Admin", "Admin role"), jwt);

    ArgumentCaptor<CreateRoleCommand> captor = ArgumentCaptor.forClass(CreateRoleCommand.class);
    verify(createRoleCommandHandler).handler(captor.capture());
    assertThat(captor.getValue().workspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(captor.getValue().invokedBy()).isEqualTo(OWNER_ID);
    assertThat(response.getHeaders().getLocation()).isNotNull().hasToString("/api/v1/roles/" + ROLE_ID);
    assertThat(response.getBody()).isEqualTo(RoleResponseDto.from(activeWorkspaceRole()));
  }

  @Test
  void shouldGetRoleById() {
    when(getRoleByIdQueryHandler.handler(any())).thenReturn(activeWorkspaceRole());

    var response = controller.getRoleById(ROLE_ID);

    assertThat(response.getBody()).isEqualTo(RoleResponseDto.from(activeWorkspaceRole()));
  }

  @Test
  void shouldUpdateRole() {
    when(jwt.getSubject()).thenReturn(OWNER_ID.toString());

    var response =
        controller.updateRole(ROLE_ID, new UpdateRoleRequestDto("New name", "New description"), jwt);

    ArgumentCaptor<UpdateRoleCommand> captor = ArgumentCaptor.forClass(UpdateRoleCommand.class);
    verify(updateRoleCommandHandler).handler(captor.capture());
    assertThat(captor.getValue().id()).isEqualTo(ROLE_ID);
    assertThat(captor.getValue().invokedBy()).isEqualTo(OWNER_ID);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }

  @Test
  void shouldUpdateRolePermissions() {
    when(jwt.getSubject()).thenReturn(OWNER_ID.toString());

    var response =
        controller.updateRolePermissions(
            ROLE_ID,
            new UpdateRolePermissionsRequestDto(
                java.util.Set.of("workspace.update"), java.util.Set.of("workspace.delete")),
            jwt);

    ArgumentCaptor<UpdateRolePermissionsCommand> captor =
        ArgumentCaptor.forClass(UpdateRolePermissionsCommand.class);
    verify(updateRolePermissionsCommandHandler).handler(captor.capture());
    assertThat(captor.getValue().roleId()).isEqualTo(ROLE_ID);
    assertThat(captor.getValue().invokedBy()).isEqualTo(OWNER_ID);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }
}
