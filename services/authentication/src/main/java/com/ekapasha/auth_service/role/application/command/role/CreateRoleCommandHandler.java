package com.ekapasha.auth_service.role.application.command.role;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.repository.RoleWriteRepository;
import com.ekapasha.auth_service.shared.application.command.CommandHandler;
import com.ekapasha.auth_service.shared.domain.exception.NotFoundException;
import com.ekapasha.auth_service.shared.domain.exception.UnauthorizedAccessException;
import com.ekapasha.auth_service.shared.domain.exception.ValidationException;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

public class CreateRoleCommandHandler implements CommandHandler<CreateRoleCommand, Role> {

  private final RoleWriteRepository roleWriteRepository;
  private final WorkspaceReadRepository workspaceReadRepository;

  public CreateRoleCommandHandler(
      RoleWriteRepository roleWriteRepository,
      WorkspaceReadRepository workspaceReadRepository
  ) {
    this.roleWriteRepository = roleWriteRepository;
    this.workspaceReadRepository = workspaceReadRepository;
  }

  @Override
  @Transactional
  public Role handler(CreateRoleCommand command) {
    // Validate workspace exists and user is the owner
    Workspace workspace = workspaceReadRepository
        .findById(command.workspaceId())
        .orElseThrow(() -> new NotFoundException("Workspace not found."));

    if (!workspace.getOwnerId().equals(command.invokedBy())) {
      throw new UnauthorizedAccessException("Only workspace owner can create roles.");
    }

    // Validate data
    if(command.name() == null || command.name().isBlank()) {
      throw new ValidationException("name", "name must not be null or blank.");
    }

    Instant now = Instant.now();

    Role newRole = Role
        .builder()
        .id(UUID.randomUUID())
        .workspaceId(command.workspaceId())
        .name(command.name())
        .description(command.description())
        .createdAt(now)
        .updatedAt(now)
        .createdBy(command.invokedBy())
        .updatedBy(command.invokedBy())
        .build();

    return this.roleWriteRepository.save(newRole);
  }
}
