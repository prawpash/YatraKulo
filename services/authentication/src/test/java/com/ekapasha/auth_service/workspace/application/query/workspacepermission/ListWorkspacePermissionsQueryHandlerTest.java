package com.ekapasha.auth_service.workspace.application.query.workspacepermission;

import com.ekapasha.auth_service.role.domain.entity.RolePermission;
import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.domain.repository.RolePermissionReadRepository;
import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberReadRepository;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.ekapasha.auth_service.workspace.WorkspaceTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListWorkspacePermissionsQueryHandlerTest {

  @Mock private WorkspaceMemberReadRepository workspaceMemberReadRepository;
  @Mock private RolePermissionReadRepository rolePermissionReadRepository;
  @InjectMocks private ListWorkspacePermissionsQueryHandler handler;

  @Test
  void shouldReturnPermissionCodesForAuthorizedUser() {
    WorkspaceMember member = workspaceMemberWithRole(ROLE_ID);
    when(workspaceMemberReadRepository.findByWorkspaceIdAndUserId(WORKSPACE_ID, OTHER_USER_ID))
        .thenReturn(java.util.Optional.of(member));
    when(rolePermissionReadRepository.findByRoleId(ROLE_ID))
        .thenReturn(List.of(rolePermission(Permission.WORKSPACE_UPDATE), rolePermission(Permission.ACCOUNT_READ)));

    List<String> result =
        handler.handler(new ListWorkspacePermissionsQuery(WORKSPACE_ID, OTHER_USER_ID, OTHER_USER_ID));

    assertThat(result).containsExactlyInAnyOrder("workspace.update", "account.read");
  }

  @Test
  void shouldRejectUnauthorizedViewer() {
    assertThatThrownBy(
            () -> handler.handler(new ListWorkspacePermissionsQuery(WORKSPACE_ID, OTHER_USER_ID, OWNER_ID)))
        .isInstanceOf(UnauthorizedAccessException.class)
        .hasMessage("You are not authorized to view this data");
  }

  @Test
  void shouldRejectMissingMember() {
    when(workspaceMemberReadRepository.findByWorkspaceIdAndUserId(WORKSPACE_ID, OTHER_USER_ID))
        .thenReturn(java.util.Optional.empty());

    assertThatThrownBy(
            () -> handler.handler(new ListWorkspacePermissionsQuery(WORKSPACE_ID, OTHER_USER_ID, OTHER_USER_ID)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("User is not a member of this workspace");
  }
}
