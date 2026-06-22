package com.ekapasha.auth_service.role.application.command.role;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.repository.RoleWriteRepository;
import com.ekapasha.shared.cqrs.CommandHandler;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.exception.UnauthorizedAccessException;
import com.ekapasha.shared.exception.ValidationException;
import com.ekapasha.shared.logging.AppLogger;
import com.ekapasha.shared.logging.LogEvent;
import com.ekapasha.auth_service.logging.AuthLogEvent;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

public class CreateRoleCommandHandler implements CommandHandler<CreateRoleCommand, Role> {

  private final RoleWriteRepository roleWriteRepository;
  private final WorkspaceReadRepository workspaceReadRepository;
  private final AppLogger logger = new AppLogger(CreateRoleCommandHandler.class);

  public CreateRoleCommandHandler(
      RoleWriteRepository roleWriteRepository, WorkspaceReadRepository workspaceReadRepository) {
    this.roleWriteRepository = roleWriteRepository;
    this.workspaceReadRepository = workspaceReadRepository;
  }

  @Override
  @Transactional
  public Role handler(CreateRoleCommand command) {
    try {
      // Validate workspace exists and user is the owner
      Workspace workspace =
          workspaceReadRepository
              .findById(command.workspaceId())
              .orElseThrow(() -> new NotFoundException("Workspace not found."));

      if (!workspace.getOwnerId().equals(command.invokedBy())) {
        throw new UnauthorizedAccessException("Only workspace owner can create roles.");
      }

      // Validate data
      if (command.name() == null || command.name().isBlank()) {
        throw new ValidationException("name", "name must not be null or blank.");
      }

      Instant now = Instant.now();

      Role newRole =
          Role.builder()
              .id(UUID.randomUUID())
              .workspaceId(command.workspaceId())
              .name(command.name())
              .description(command.description())
              .createdAt(now)
              .updatedAt(now)
              .createdBy(command.invokedBy())
              .updatedBy(command.invokedBy())
              .build();

      Role savedRole = this.roleWriteRepository.save(newRole);

      this.logger.info(
          LogEvent.builder("Role created successfully: " + savedRole.getName())
              .eventName(AuthLogEvent.ROLE_CREATED)
              .metadata("role.id", savedRole.getId().toString())
              .metadata("role.name", savedRole.getName())
              .metadata(
                  "workspace.id", savedRole.getWorkspaceId().map(UUID::toString).orElse("null"))
              .build());

      return savedRole;
    } catch (Exception e) {
      String workspaceId =
          command.workspaceId() != null ? command.workspaceId().toString() : "null";
      if (e instanceof NotFoundException
          || e instanceof UnauthorizedAccessException
          || e instanceof ValidationException) {
        this.logger.warn(
            LogEvent.builder("Role creation failed: " + e.getMessage())
                .eventName(AuthLogEvent.ROLE_OPERATION_FAILED)
                .metadata("role.name", command.name())
                .metadata("workspace.id", workspaceId)
                .error(e)
                .build());
      } else {
        this.logger.error(
            LogEvent.builder("Role creation failed with system error: " + e.getMessage())
                .eventName(AuthLogEvent.ROLE_OPERATION_FAILED)
                .metadata("role.name", command.name())
                .metadata("workspace.id", workspaceId)
                .error(e)
                .build());
      }
      throw e;
    }
  }
}
