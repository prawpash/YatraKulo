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
public class UpdateWorkspaceCommandHandler implements VoidCommandHandler<UpdateWorkspaceCommand> {

  private final WorkspaceWriteRepository workspaceWriteRepository;
  private final WorkspaceReadRepository workspaceReadRepository;
  private final AppLogger logger = new AppLogger(UpdateWorkspaceCommandHandler.class);

  @Override
  @Transactional
  public void handler(UpdateWorkspaceCommand command) {
    try {
      // Only the owner can update the workspace - use findByIdAndOwnerId for authorization
      // Returns NotFoundException if workspace doesn't exist OR user is not the owner
      Workspace workspace =
          this.workspaceReadRepository
              .findByIdAndOwnerId(command.id(), command.invokedBy())
              .orElseThrow(() -> new NotFoundException("Workspace not found."));

      Instant now = Instant.now();

      // Update name if provided
      if (command.name() != null && !command.name().isBlank()) {
        workspace.rename(command.name(), now);
      }

      // Update description if provided and different
      if (command.description() != null) {
        workspace.updateDescription(command.description(), now);
      }

      // Save the updated workspace
      this.workspaceWriteRepository.save(workspace);

      this.logger.info(
          LogEvent.builder("Workspace updated successfully: " + workspace.getName())
              .eventName(AuthLogEvent.WORKSPACE_UPDATED)
              .metadata("workspace.id", workspace.getId().toString())
              .metadata("workspace.name", workspace.getName())
              .build());
    } catch (Exception e) {
      String workspaceId = command.id() != null ? command.id()
                                                   .toString() : "null";
      if (e instanceof NotFoundException) {
        this.logger.warn(
            LogEvent.builder("Workspace update failed: " + e.getMessage())
                .eventName(AuthLogEvent.WORKSPACE_OPERATION_FAILED)
                .metadata("workspace.id", workspaceId)
                .error(e)
                .build());
      } else {
        this.logger.error(
            LogEvent.builder("Workspace update failed with system error: " + e.getMessage())
                .eventName(AuthLogEvent.WORKSPACE_OPERATION_FAILED)
                .metadata("workspace.id", workspaceId)
                .error(e)
                .build());
      }
      throw e;
    }
  }
}
