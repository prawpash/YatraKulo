package com.ekapasha.auth_service.workspace.application.command.workspace;

import com.ekapasha.auth_service.shared.application.command.VoidCommandHandler;
import com.ekapasha.auth_service.shared.domain.exception.DomainRuleViolationException;
import com.ekapasha.auth_service.shared.domain.exception.NotFoundException;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class DeleteWorkspaceCommandHandler implements VoidCommandHandler<DeleteWorkspaceCommand> {

  private final WorkspaceWriteRepository workspaceWriteRepository;
  private final WorkspaceReadRepository workspaceReadRepository;

  @Override
  @Transactional
  public void handler(DeleteWorkspaceCommand command) {
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
  }
}
