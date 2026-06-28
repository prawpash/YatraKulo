package com.ekapasha.auth_service.workspace.infrastructure.config;

import com.ekapasha.auth_service.role.domain.repository.RolePermissionReadRepository;
import com.ekapasha.auth_service.workspace.application.command.workspace.CreateWorkspaceCommandHandler;
import com.ekapasha.auth_service.workspace.application.command.workspace.DeleteWorkspaceCommandHandler;
import com.ekapasha.auth_service.workspace.application.command.workspace.SetWorkspaceDefaultCommandHandler;
import com.ekapasha.auth_service.workspace.application.command.workspace.UpdateWorkspaceCommandHandler;
import com.ekapasha.auth_service.workspace.application.command.workspacemember.AddWorkspaceMemberCommandHandler;
import com.ekapasha.auth_service.workspace.application.command.workspacemember.RemoveWorkspaceMemberCommandHandler;
import com.ekapasha.auth_service.workspace.application.command.workspacemember.UpdateWorkspaceMemberRoleCommandHandler;
import com.ekapasha.auth_service.workspace.application.query.workspace.CheckWorkspaceExistsQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspace.CountWorkspacesQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspace.GetDefaultWorkspaceByOwnerQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspace.GetWorkspaceByIdAndOwnerIdQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspace.GetWorkspaceByIdQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspace.ListWorkspacesByOwnerQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspacemember.CheckWorkspaceMemberExistsQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspacemember.CountWorkspaceMembersQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspacemember.GetWorkspaceMemberByIdQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspacemember.GetWorkspaceMemberByWorkspaceAndUserQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspacemember.ListUserWorkspaceMembershipsQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspacemember.ListWorkspaceMembersByWorkspaceQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspacepermission.ListWorkspacePermissionsQueryHandler;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberReadRepository;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberWriteRepository;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceWriteRepository;
import com.ekapasha.auth_service.workspace.domain.service.WorkspaceMemberService;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class WorkspaceConfigTest {

  @Test
  void shouldCreateWorkspaceBeans() {
    WorkspaceConfig config = new WorkspaceConfig();

    JPAWorkspaceRepository workspaceRepository = Mockito.mock(JPAWorkspaceRepository.class);
    JPAWorkspaceMemberRepository workspaceMemberRepository = Mockito.mock(JPAWorkspaceMemberRepository.class);
    RolePermissionReadRepository rolePermissionReadRepository = Mockito.mock(RolePermissionReadRepository.class);

    WorkspaceWriteRepository workspaceWriteRepository = config.workspaceWriteRepository(workspaceRepository);
    WorkspaceReadRepository workspaceReadRepository = config.workspaceReadRepository(workspaceRepository);
    WorkspaceMemberWriteRepository workspaceMemberWriteRepository =
        config.workspaceMemberWriteRepository(workspaceMemberRepository);
    WorkspaceMemberReadRepository workspaceMemberReadRepository =
        config.workspaceMemberReadRepository(workspaceMemberRepository);
    WorkspaceMemberService workspaceMemberService =
        config.workspaceMemberService(workspaceMemberReadRepository, workspaceMemberWriteRepository);

    assertThat(workspaceWriteRepository).isInstanceOf(WorkspaceWriteRepositoryImpl.class);
    assertThat(workspaceReadRepository).isInstanceOf(WorkspaceReadRepositoryImpl.class);
    assertThat(workspaceMemberWriteRepository).isInstanceOf(WorkspaceMemberWriteRepositoryImpl.class);
    assertThat(workspaceMemberReadRepository).isInstanceOf(WorkspaceMemberReadRepositoryImpl.class);
    assertThat(workspaceMemberService).isNotNull();

    assertThat(config.createWorkspaceCommandHandler(workspaceWriteRepository, workspaceMemberService))
        .isInstanceOf(CreateWorkspaceCommandHandler.class);
    assertThat(config.updateWorkspaceCommandHandler(workspaceWriteRepository, workspaceReadRepository))
        .isInstanceOf(UpdateWorkspaceCommandHandler.class);
    assertThat(config.deleteWorkspaceCommandHandler(workspaceWriteRepository, workspaceReadRepository))
        .isInstanceOf(DeleteWorkspaceCommandHandler.class);
    assertThat(config.setWorkspaceDefaultCommandHandler(workspaceWriteRepository, workspaceReadRepository))
        .isInstanceOf(SetWorkspaceDefaultCommandHandler.class);
    assertThat(config.addWorkspaceMemberCommandHandler(workspaceMemberService, workspaceReadRepository))
        .isInstanceOf(AddWorkspaceMemberCommandHandler.class);
    assertThat(config.removeWorkspaceMemberCommandHandler(workspaceMemberService, workspaceReadRepository))
        .isInstanceOf(RemoveWorkspaceMemberCommandHandler.class);
    assertThat(config.updateWorkspaceMemberRoleCommandHandler(workspaceMemberService, workspaceReadRepository))
        .isInstanceOf(UpdateWorkspaceMemberRoleCommandHandler.class);
    assertThat(config.getWorkspaceByIdQueryHandler(workspaceReadRepository))
        .isInstanceOf(GetWorkspaceByIdQueryHandler.class);
    assertThat(config.getWorkspaceByIdAndOwnerIdQueryHandler(workspaceReadRepository))
        .isInstanceOf(GetWorkspaceByIdAndOwnerIdQueryHandler.class);
    assertThat(config.getDefaultWorkspaceByOwnerQueryHandler(workspaceReadRepository))
        .isInstanceOf(GetDefaultWorkspaceByOwnerQueryHandler.class);
    assertThat(config.listWorkspacesByOwnerQueryHandler(workspaceReadRepository))
        .isInstanceOf(ListWorkspacesByOwnerQueryHandler.class);
    assertThat(config.checkWorkspaceExistsQueryHandler(workspaceReadRepository))
        .isInstanceOf(CheckWorkspaceExistsQueryHandler.class);
    assertThat(config.countWorkspacesQueryHandler(workspaceReadRepository))
        .isInstanceOf(CountWorkspacesQueryHandler.class);
    assertThat(config.getWorkspaceMemberByIdQueryHandler(workspaceMemberReadRepository))
        .isInstanceOf(GetWorkspaceMemberByIdQueryHandler.class);
    assertThat(config.getWorkspaceMemberByWorkspaceAndUserQueryHandler(workspaceMemberReadRepository))
        .isInstanceOf(GetWorkspaceMemberByWorkspaceAndUserQueryHandler.class);
    assertThat(config.listWorkspaceMembersByWorkspaceQueryHandler(workspaceMemberReadRepository))
        .isInstanceOf(ListWorkspaceMembersByWorkspaceQueryHandler.class);
    assertThat(config.listUserWorkspaceMembershipsQueryHandler(workspaceMemberReadRepository))
        .isInstanceOf(ListUserWorkspaceMembershipsQueryHandler.class);
    assertThat(config.checkWorkspaceMemberExistsQueryHandler(workspaceMemberReadRepository))
        .isInstanceOf(CheckWorkspaceMemberExistsQueryHandler.class);
    assertThat(config.countWorkspaceMembersQueryHandler(workspaceMemberReadRepository))
        .isInstanceOf(CountWorkspaceMembersQueryHandler.class);
    assertThat(config.listWorkspacePermissionsQueryHandler(workspaceMemberReadRepository, rolePermissionReadRepository))
        .isInstanceOf(ListWorkspacePermissionsQueryHandler.class);
  }
}
