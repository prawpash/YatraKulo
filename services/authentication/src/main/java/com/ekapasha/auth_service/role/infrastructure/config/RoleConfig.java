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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleConfig {

  //  Repository Beans
  @Bean
  public RoleWriteRepository roleWriteRepository(JPARoleRepository jpaRoleRepository) {
    return new RoleWriteRepositoryImpl(jpaRoleRepository);
  }

  @Bean
  public RoleReadRepository roleReadRepository(JPARoleRepository jpaRoleRepository) {
    return new RoleReadRepositoryImpl(jpaRoleRepository);
  }

  @Bean
  public PermissionReadRepository permissionReadRepository(
      JPAPermissionRepository jpaPermissionRepository) {
    return new PermissionReadRepositoryImpl(jpaPermissionRepository);
  }

  @Bean
  public RolePermissionWriteRepository rolePermissionWriteRepository(
      JPARolePermissionRepository jpaRolePermissionRepository) {
    return new RolePermissionWriteRepositoryImpl(jpaRolePermissionRepository);
  }

  @Bean
  public RolePermissionReadRepository rolePermissionReadRepository(
      JPARolePermissionRepository jpaRolePermissionRepository) {
    return new RolePermissionReadRepositoryImpl(jpaRolePermissionRepository);
  }

  @Bean
  public RolePermissionService rolePermissionService(
      RolePermissionReadRepository rolePermissionReadRepository,
      RolePermissionWriteRepository rolePermissionWriteRepository) {
    return new RolePermissionService(rolePermissionReadRepository, rolePermissionWriteRepository);
  }

  //  Command Handler Beans
  @Bean
  public CreateRoleCommandHandler createRoleCommandHandler(
      RoleWriteRepository roleWriteRepository, WorkspaceReadRepository workspaceReadRepository) {
    return new CreateRoleCommandHandler(roleWriteRepository, workspaceReadRepository);
  }

  @Bean
  public UpdateRoleCommandHandler updateRoleCommandHandler(
      RoleWriteRepository roleWriteRepository, RoleReadRepository roleReadRepository) {
    return new UpdateRoleCommandHandler(roleWriteRepository, roleReadRepository);
  }

  @Bean
  public UpdateRolePermissionsCommandHandler updateRolePermissionsCommandHandler(
      RoleReadRepository roleReadRepository,
      WorkspaceReadRepository workspaceReadRepository,
      RolePermissionService rolePermissionService) {
    return new UpdateRolePermissionsCommandHandler(
        roleReadRepository, workspaceReadRepository, rolePermissionService);
  }

  //  Query Handler Beans
  //  Role
  @Bean
  public GetRoleByIdQueryHandler getRoleByIdQueryHandler(RoleReadRepository roleReadRepository) {
    return new GetRoleByIdQueryHandler(roleReadRepository);
  }

  @Bean
  public ListRolesQueryHandler getRolesByNameQueryHandler(RoleReadRepository roleReadRepository) {
    return new ListRolesQueryHandler(roleReadRepository);
  }

  //  Permission
  @Bean
  public GetPermissionByCodeQueryHandler getPermissionByCodeQueryHandler(
      PermissionReadRepository permissionReadRepository) {
    return new GetPermissionByCodeQueryHandler(permissionReadRepository);
  }

  @Bean
  public ListPermissionsQueryHandler listPermissionsQueryHandler(
      PermissionReadRepository permissionReadRepository) {
    return new ListPermissionsQueryHandler(permissionReadRepository);
  }
}
