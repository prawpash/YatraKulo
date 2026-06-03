package com.ekapasha.auth_service.workspace.application.command.workspace;

import com.ekapasha.shared.cqrs.VoidCommandHandler;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.logging.AppLogger;
import com.ekapasha.shared.logging.LogEvent;
import com.ekapasha.auth_service.logging.AuthLogEvent;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
public class SetWorkspaceDefaultCommandHandler
    implements VoidCommandHandler<SetWorkspaceDefaultCommand> {

  private final WorkspaceWriteRepository workspaceWriteRepository;
  private final WorkspaceReadRepository workspaceReadRepository;
  private final AppLogger logger = new AppLogger(SetWorkspaceDefaultCommandHandler.class);

  @Override
  @Transactional
  public void handler(SetWorkspaceDefaultCommand command) {
    try {
      // Only the owner can set/unset default - use findByIdAndOwnerId for authorization
      // Returns NotFoundException if workspace doesn't exist OR user is not the owner
      Workspace workspace =
          this.workspaceReadRepository
              .findByIdAndOwnerId(command.id(), command.invokedBy())
              .orElseThrow(() -> new NotFoundException("Workspace not found."));

      Instant now = Instant.now();

      // Only update if the default status is different
      if (command.isDefault()) {
        // Unset the current default workspace first (only one default per owner)
        this.workspaceReadRepository
            .findDefaultByOwnerId(command.invokedBy())
            .ifPresent(
                currentDefault -> {
                  currentDefault.unsetDefault(now);
                  this.workspaceWriteRepository.save(currentDefault);
                });

        // Set the new workspace as default
        workspace.markAsDefault(now);
      } else {
        workspace.unsetDefault(now);
      }

      // Save the updated workspace
      this.workspaceWriteRepository.save(workspace);

      this.logger.info(
          LogEvent.builder("Workspace set as default successfully: " + workspace.getName())
              .eventName(AuthLogEvent.WORKSPACE_DEFAULT_SET)
              .metadata("workspace.id", workspace.getId().toString())
              .metadata("workspace.name", workspace.getName())
              .build());
    } catch (Exception e) {
      String workspaceId = command.id() != null ? command.id().toString() : "null";
      if (e instanceof NotFoundException) {
        this.logger.warn(
            LogEvent.builder("Setting workspace as default failed: " + e.getMessage())
                .eventName(AuthLogEvent.WORKSPACE_OPERATION_FAILED)
                .metadata("workspace.id", workspaceId)
                .error(e)
                .build());
      } else {
        this.logger.error(
            LogEvent.builder(
                    "Setting workspace as default failed with system error: " + e.getMessage())
                .eventName(AuthLogEvent.WORKSPACE_OPERATION_FAILED)
                .metadata("workspace.id", workspaceId)
                .error(e)
                .build());
      }
      throw e;
    }
  }
}
