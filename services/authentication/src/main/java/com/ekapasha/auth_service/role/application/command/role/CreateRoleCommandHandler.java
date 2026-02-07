package com.ekapasha.auth_service.role.application.command.role;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.repository.RoleWriteRepository;
import com.ekapasha.auth_service.shared.application.command.CommandHandler;
import com.ekapasha.auth_service.shared.domain.exception.ValidationException;

import java.time.Instant;
import java.util.UUID;

public class CreateRoleCommandHandler implements CommandHandler<CreateRoleCommand, Role> {

  private final RoleWriteRepository roleWriteRepository;

  public CreateRoleCommandHandler(
      RoleWriteRepository roleWriteRepository
  ) {
    this.roleWriteRepository = roleWriteRepository;
  }

  @Override
  public Role handler(CreateRoleCommand command) {
    // TODO: Check permission

    // Validate data
    if(command.name() == null || command.name().isBlank()) {
      throw new ValidationException("name", "name must not be null or blank.");
    }

    Instant now = Instant.now();

    Role newRole = Role
        .builder()
        .id(UUID.randomUUID())
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
