package com.ekapasha.auth_service.workspace.application.command.workspace;

import com.ekapasha.auth_service.shared.application.command.VoidCommandHandler;
import com.ekapasha.auth_service.shared.domain.exception.NotFoundException;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceWriteRepository;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@RequiredArgsConstructor
public class SetWorkspaceDefaultCommandHandler
    implements VoidCommandHandler<SetWorkspaceDefaultCommand> {

  private final WorkspaceWriteRepository workspaceWriteRepository;
  private final WorkspaceReadRepository workspaceReadRepository;

  @Override
  public void handler(SetWorkspaceDefaultCommand command) {
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
  }
}
