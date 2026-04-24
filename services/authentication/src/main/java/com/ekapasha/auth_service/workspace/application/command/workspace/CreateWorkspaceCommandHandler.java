package com.ekapasha.auth_service.workspace.application.command.workspace;

import com.ekapasha.shared.cqrs.CommandHandler;
import com.ekapasha.shared.exception.ValidationException;
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

  @Override
  @Transactional
  public Workspace handler(CreateWorkspaceCommand command) {
    // Validate data
    if (command.name() == null || command.name().isBlank()) {
      throw new ValidationException("name", "name must not be null or blank.");
    }

    if (command.ownerId() == null) {
      throw new ValidationException("ownerId", "ownerId must not be null.");
    }

    Instant now = Instant.now();

    Workspace newWorkspace = Workspace.builder()
        .id(UUID.randomUUID())
        .name(command.name())
        .description(command.description())
        .ownerId(command.ownerId())
        .isDefault(false) // Handle the set default true in the next code to prevent race condition
        .createdAt(now)
        .updatedAt(now)
        .build();

    if (command.isDefault()) {
      this.workspaceWriteRepository.setDefault(newWorkspace.getId(), command.ownerId());
    }

    return this.workspaceWriteRepository.save(newWorkspace);
  }
}
