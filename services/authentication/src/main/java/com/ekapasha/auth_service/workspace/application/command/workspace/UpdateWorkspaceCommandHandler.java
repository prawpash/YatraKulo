package com.ekapasha.auth_service.workspace.application.command.workspace;

import com.ekapasha.shared.cqrs.VoidCommandHandler;
import com.ekapasha.shared.exception.NotFoundException;
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

  @Override
  @Transactional
  public void handler(UpdateWorkspaceCommand command) {
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
  }
}
