package com.ekapasha.auth_service.role.infrastructure.config;

import com.ekapasha.auth_service.role.application.command.role.CreateRoleCommandHandler;
import com.ekapasha.auth_service.role.application.command.role.UpdateRoleCommandHandler;
import com.ekapasha.auth_service.role.application.command.role.UpdateRolePermissionsCommandHandler;
import com.ekapasha.auth_service.role.application.query.permission.GetPermissionByCodeQueryHandler;
import com.ekapasha.auth_service.role.application.query.permission.ListPermissionsQueryHandler;
import com.ekapasha.auth_service.role.application.query.role.GetRoleByIdQueryHandler;
import com.ekapasha.auth_service.role.application.query.role.ListRolesQueryHandler;
import com.ekapasha.auth_service.role.domain.repository.PermissionReadRepository;
import com.ekapasha.auth_service.role.domain.repository.RolePermissionReadRepository;
import com.ekapasha.auth_service.role.domain.repository.RolePermissionWriteRepository;
import com.ekapasha.auth_service.role.domain.repository.RoleReadRepository;
import com.ekapasha.auth_service.role.domain.repository.RoleWriteRepository;
import com.ekapasha.auth_service.role.domain.service.RolePermissionService;
import com.ekapasha.auth_service.role.infrastructure.persistence.repository.*;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class RoleConfigTest {

  @Test
  void shouldCreateRoleBeans() {
    RoleConfig config = new RoleConfig();

    JPARoleRepository roleRepository = Mockito.mock(JPARoleRepository.class);
    JPAPermissionRepository permissionRepository = Mockito.mock(JPAPermissionRepository.class);
    JPARolePermissionRepository rolePermissionRepository = Mockito.mock(JPARolePermissionRepository.class);
    WorkspaceReadRepository workspaceReadRepository = Mockito.mock(WorkspaceReadRepository.class);
    RoleReadRepository roleReadRepository = config.roleReadRepository(roleRepository);
    RoleWriteRepository roleWriteRepository = config.roleWriteRepository(roleRepository);
    PermissionReadRepository permissionReadRepository = config.permissionReadRepository(permissionRepository);
    RolePermissionReadRepository rolePermissionReadRepository = config.rolePermissionReadRepository(rolePermissionRepository);
    RolePermissionWriteRepository rolePermissionWriteRepository = config.rolePermissionWriteRepository(rolePermissionRepository);
    RolePermissionService rolePermissionService = config.rolePermissionService(rolePermissionReadRepository, rolePermissionWriteRepository);

    assertThat(roleReadRepository).isInstanceOf(RoleReadRepositoryImpl.class);
    assertThat(roleWriteRepository).isInstanceOf(RoleWriteRepositoryImpl.class);
    assertThat(permissionReadRepository).isInstanceOf(PermissionReadRepositoryImpl.class);
    assertThat(rolePermissionReadRepository).isInstanceOf(RolePermissionReadRepositoryImpl.class);
    assertThat(rolePermissionWriteRepository).isInstanceOf(RolePermissionWriteRepositoryImpl.class);
    assertThat(rolePermissionService).isNotNull();

    assertThat(config.createRoleCommandHandler(roleWriteRepository, workspaceReadRepository))
        .isInstanceOf(CreateRoleCommandHandler.class);
    assertThat(config.updateRoleCommandHandler(roleWriteRepository, roleReadRepository))
        .isInstanceOf(UpdateRoleCommandHandler.class);
    assertThat(
            config.updateRolePermissionsCommandHandler(
                roleReadRepository, workspaceReadRepository, rolePermissionService))
        .isInstanceOf(UpdateRolePermissionsCommandHandler.class);
    assertThat(config.getRoleByIdQueryHandler(roleReadRepository))
        .isInstanceOf(GetRoleByIdQueryHandler.class);
    assertThat(config.getRolesByNameQueryHandler(roleReadRepository))
        .isInstanceOf(ListRolesQueryHandler.class);
    assertThat(config.getPermissionByCodeQueryHandler(permissionReadRepository))
        .isInstanceOf(GetPermissionByCodeQueryHandler.class);
    assertThat(config.listPermissionsQueryHandler(permissionReadRepository))
        .isInstanceOf(ListPermissionsQueryHandler.class);
  }
}
