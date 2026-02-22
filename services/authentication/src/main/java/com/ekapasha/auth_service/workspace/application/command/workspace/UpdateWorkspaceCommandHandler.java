package com.ekapasha.auth_service.workspace.application.command.workspace;

import com.ekapasha.auth_service.shared.application.command.VoidCommandHandler;
import com.ekapasha.auth_service.shared.domain.exception.NotFoundException;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceWriteRepository;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@RequiredArgsConstructor
public class UpdateWorkspaceCommandHandler implements VoidCommandHandler<UpdateWorkspaceCommand> {

  private final WorkspaceWriteRepository workspaceWriteRepository;
  private final WorkspaceReadRepository workspaceReadRepository;

  @Override
  public void handler(UpdateWorkspaceCommand command) {
    // Only the owner can update the workspace - use findByIdAndOwnerId for authorization
    // Returns NotFoundException if workspace doesn't exist OR user is not the owner
    Workspace workspace = this.workspaceReadRepository
        .findByIdAndOwnerId(command.id(), command.invokedBy())
        .orElseThrow(() -> new NotFoundException("Workspace not found."));

    Instant now = Instant.now();

    // Update name if provided and different
    if (command.name() != null
        && !command.name().isBlank()
        && !command.name().equals(workspace.getName())) {
      workspace.rename(command.name(), now);
    }

    // Update description if provided and different
    if (command.description() != null
        && !command.description().equals(workspace.getDescription().orElse(null))) {
      workspace.updateDescription(command.description(), now);
    }

    // Save the updated workspace
    this.workspaceWriteRepository.save(workspace);
  }
}
