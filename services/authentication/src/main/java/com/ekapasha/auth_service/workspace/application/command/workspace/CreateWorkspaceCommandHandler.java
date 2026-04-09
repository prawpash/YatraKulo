package com.ekapasha.auth_service.workspace.application.command.workspace;

import com.ekapasha.auth_service.shared.application.command.CommandHandler;
import com.ekapasha.auth_service.shared.domain.exception.ValidationException;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class CreateWorkspaceCommandHandler
    implements CommandHandler<CreateWorkspaceCommand, Workspace> {

  private final WorkspaceWriteRepository workspaceWriteRepository;
  private final WorkspaceReadRepository workspaceReadRepository;

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

    Workspace newWorkspace =
        Workspace.builder()
            .id(UUID.randomUUID())
            .name(command.name())
            .description(command.description())
            .ownerId(command.ownerId())
            .isDefault(command.isDefault())
            .createdAt(now)
            .updatedAt(now)
            .build();

    if (command.isDefault()) {
      this.workspaceReadRepository
          .findDefaultByOwnerId(command.ownerId())
          .ifPresent(
              currentDefault -> {
                currentDefault.unsetDefault(now);
                this.workspaceWriteRepository.save(currentDefault);
              });
    }

    return this.workspaceWriteRepository.save(newWorkspace);
  }
}
