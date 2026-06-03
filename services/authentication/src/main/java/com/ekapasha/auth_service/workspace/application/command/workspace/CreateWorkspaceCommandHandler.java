package com.ekapasha.auth_service.workspace.application.command.workspace;

import com.ekapasha.shared.cqrs.CommandHandler;
import com.ekapasha.shared.exception.ValidationException;
import com.ekapasha.shared.logging.AppLogger;
import com.ekapasha.shared.logging.LogEvent;
import com.ekapasha.auth_service.logging.AuthLogEvent;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class CreateWorkspaceCommandHandler
    implements CommandHandler<CreateWorkspaceCommand, Workspace> {

  private final WorkspaceWriteRepository workspaceWriteRepository;
  private final AppLogger logger = new AppLogger(CreateWorkspaceCommandHandler.class);

  @Override
  @Transactional
  public Workspace handler(CreateWorkspaceCommand command) {
    try {
      // Validate data
      if (command.name() == null || command.name().isBlank()) {
        throw new ValidationException("name", "name must not be null or blank.");
      }

      if (command.ownerId() == null) {
        throw new ValidationException("ownerId", "ownerId must not be null.");
      }

      Instant now = Instant.now();

      Workspace newWorkspace =
          Workspace.builder()
              .id(UUID.randomUUID())
              .name(command.name())
              .description(command.description())
              .ownerId(command.ownerId())
              .isDefault(
                  false) // Handle the set default true in the next code to prevent race condition
              .createdAt(now)
              .updatedAt(now)
              .build();

      if (command.isDefault()) {
        this.workspaceWriteRepository.setDefault(newWorkspace.getId(), command.ownerId());
      }

      Workspace savedWorkspace = this.workspaceWriteRepository.save(newWorkspace);

      this.logger.info(
          LogEvent.builder("Workspace created successfully: " + savedWorkspace.getName())
              .eventName(AuthLogEvent.WORKSPACE_CREATED)
              .metadata("workspace.id", savedWorkspace.getId().toString())
              .metadata("workspace.name", savedWorkspace.getName())
              .metadata("user.id", savedWorkspace.getOwnerId().toString())
              .build());

      return savedWorkspace;
    } catch (Exception e) {
      String ownerId = command.ownerId() != null ? command.ownerId().toString() : "null";
      if (e instanceof ValidationException) {
        this.logger.warn(
            LogEvent.builder("Workspace creation failed: " + e.getMessage())
                .eventName(AuthLogEvent.WORKSPACE_OPERATION_FAILED)
                .metadata("workspace.name", command.name())
                .metadata("user.id", ownerId)
                .error(e)
                .build());
      } else {
        this.logger.error(
            LogEvent.builder("Workspace creation failed with system error: " + e.getMessage())
                .eventName(AuthLogEvent.WORKSPACE_OPERATION_FAILED)
                .metadata("workspace.name", command.name())
                .metadata("user.id", ownerId)
                .error(e)
                .build());
      }
      throw e;
    }
  }
}
