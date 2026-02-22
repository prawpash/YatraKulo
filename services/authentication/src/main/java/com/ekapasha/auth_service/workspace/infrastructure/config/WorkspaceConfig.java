package com.ekapasha.auth_service.workspace.infrastructure.config;

import com.ekapasha.auth_service.workspace.application.command.workspace.CreateWorkspaceCommandHandler;
import com.ekapasha.auth_service.workspace.application.command.workspace.DeleteWorkspaceCommandHandler;
import com.ekapasha.auth_service.workspace.application.command.workspace.SetWorkspaceDefaultCommandHandler;
import com.ekapasha.auth_service.workspace.application.command.workspace.UpdateWorkspaceCommandHandler;
import com.ekapasha.auth_service.workspace.application.query.workspace.CheckWorkspaceExistsQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspace.CountWorkspacesQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspace.GetDefaultWorkspaceByOwnerQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspace.GetWorkspaceByIdAndOwnerIdQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspace.GetWorkspaceByIdQueryHandler;
import com.ekapasha.auth_service.workspace.application.query.workspace.ListWorkspacesByOwnerQueryHandler;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceWriteRepository;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.repository.JPAWorkspaceRepository;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.repository.WorkspaceReadRepositoryImpl;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.repository.WorkspaceWriteRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkspaceConfig {

  // ===========================================
  // Repository Beans
  // ===========================================

  @Bean
  public WorkspaceWriteRepository workspaceWriteRepository(JPAWorkspaceRepository jpaWorkspaceRepository) {
    return new WorkspaceWriteRepositoryImpl(jpaWorkspaceRepository);
  }

  @Bean
  public WorkspaceReadRepository workspaceReadRepository(JPAWorkspaceRepository jpaWorkspaceRepository) {
    return new WorkspaceReadRepositoryImpl(jpaWorkspaceRepository);
  }

  // ===========================================
  // Command Handler Beans
  // ===========================================

  @Bean
  public CreateWorkspaceCommandHandler createWorkspaceCommandHandler(
      WorkspaceWriteRepository workspaceWriteRepository) {
    return new CreateWorkspaceCommandHandler(workspaceWriteRepository);
  }

  @Bean
  public UpdateWorkspaceCommandHandler updateWorkspaceCommandHandler(
      WorkspaceWriteRepository workspaceWriteRepository,
      WorkspaceReadRepository workspaceReadRepository) {
    return new UpdateWorkspaceCommandHandler(workspaceWriteRepository, workspaceReadRepository);
  }

  @Bean
  public DeleteWorkspaceCommandHandler deleteWorkspaceCommandHandler(
      WorkspaceWriteRepository workspaceWriteRepository,
      WorkspaceReadRepository workspaceReadRepository) {
    return new DeleteWorkspaceCommandHandler(workspaceWriteRepository, workspaceReadRepository);
  }

  @Bean
  public SetWorkspaceDefaultCommandHandler setWorkspaceDefaultCommandHandler(
      WorkspaceWriteRepository workspaceWriteRepository,
      WorkspaceReadRepository workspaceReadRepository) {
    return new SetWorkspaceDefaultCommandHandler(workspaceWriteRepository, workspaceReadRepository);
  }

  // ===========================================
  // Query Handler Beans
  // ===========================================

  @Bean
  public GetWorkspaceByIdQueryHandler getWorkspaceByIdQueryHandler(
      WorkspaceReadRepository workspaceReadRepository) {
    return new GetWorkspaceByIdQueryHandler(workspaceReadRepository);
  }

  @Bean
  public GetWorkspaceByIdAndOwnerIdQueryHandler getWorkspaceByIdAndOwnerIdQueryHandler(
      WorkspaceReadRepository workspaceReadRepository) {
    return new GetWorkspaceByIdAndOwnerIdQueryHandler(workspaceReadRepository);
  }

  @Bean
  public GetDefaultWorkspaceByOwnerQueryHandler getDefaultWorkspaceByOwnerQueryHandler(
      WorkspaceReadRepository workspaceReadRepository) {
    return new GetDefaultWorkspaceByOwnerQueryHandler(workspaceReadRepository);
  }

  @Bean
  public ListWorkspacesByOwnerQueryHandler listWorkspacesByOwnerQueryHandler(
      WorkspaceReadRepository workspaceReadRepository) {
    return new ListWorkspacesByOwnerQueryHandler(workspaceReadRepository);
  }

  @Bean
  public CheckWorkspaceExistsQueryHandler checkWorkspaceExistsQueryHandler(
      WorkspaceReadRepository workspaceReadRepository) {
    return new CheckWorkspaceExistsQueryHandler(workspaceReadRepository);
  }

  @Bean
  public CountWorkspacesQueryHandler countWorkspacesQueryHandler(
      WorkspaceReadRepository workspaceReadRepository) {
    return new CountWorkspacesQueryHandler(workspaceReadRepository);
  }
}
