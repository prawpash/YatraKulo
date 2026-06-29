package com.ekapasha.auth_service.workspace.application.command.workspace;

import com.ekapasha.shared.cqrs.VoidCommandHandler;
import com.ekapasha.shared.exception.DomainRuleViolationException;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.logging.AppLogger;
import com.ekapasha.shared.logging.LogEvent;
import com.ekapasha.auth_service.shared.logging.AuthLogEvent;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class DeleteWorkspaceCommandHandler implements VoidCommandHandler<DeleteWorkspaceCommand> {

  private final WorkspaceWriteRepository workspaceWriteRepository;
  private final WorkspaceReadRepository workspaceReadRepository;
  private static final AppLogger logger = new AppLogger(DeleteWorkspaceCommandHandler.class);

  @Override
  @Transactional
  public void handler(DeleteWorkspaceCommand command) {
    try {
      // Only the owner can delete the workspace - use findByIdAndOwnerId for authorization
      // Returns NotFoundException if workspace doesn't exist OR user is not the owner
      Workspace workspace =
          this.workspaceReadRepository
              .findByIdAndOwnerId(command.id(), command.invokedBy())
              .orElseThrow(() -> new NotFoundException("Workspace not found."));

      // Cannot delete the default workspace
      if (workspace.isDefault()) {
        throw new DomainRuleViolationException(
            "Cannot delete the default workspace. Set another workspace as default first.");
      }

      // Delete the workspace
      this.workspaceWriteRepository.deleteById(command.id());

      logger.info(
          LogEvent.builder("Workspace deleted successfully: " + workspace.getName())
              .eventName(AuthLogEvent.WORKSPACE_DELETED)
              .metadata("workspace.id", workspace.getId().toString())
              .metadata("workspace.name", workspace.getName())
              .build());
    } catch (Exception e) {
      String workspaceId = command.id() != null ? command.id().toString() : "null";
      if (e instanceof NotFoundException || e instanceof DomainRuleViolationException) {
        logger.warn(
            LogEvent.builder("Workspace deletion failed: " + e.getMessage())
                .eventName(AuthLogEvent.WORKSPACE_OPERATION_FAILED)
                .metadata("workspace.id", workspaceId)
                .error(e)
                .build());
      } else {
        logger.error(
            LogEvent.builder("Workspace deletion failed with system error: " + e.getMessage())
                .eventName(AuthLogEvent.WORKSPACE_OPERATION_FAILED)
                .metadata("workspace.id", workspaceId)
                .error(e)
                .build());
      }
      throw e;
    }
  }
}
