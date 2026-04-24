package com.ekapasha.auth_service.role.application.command.role;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.repository.RoleReadRepository;
import com.ekapasha.auth_service.role.domain.repository.RoleWriteRepository;
import com.ekapasha.shared.cqrs.VoidCommandHandler;
import com.ekapasha.shared.exception.NotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public class UpdateRoleCommandHandler implements VoidCommandHandler<UpdateRoleCommand> {
  private final RoleWriteRepository roleWriteRepository;
  private final RoleReadRepository roleReadRepository;

  public UpdateRoleCommandHandler(
      RoleWriteRepository roleWriteRepository, RoleReadRepository roleReadRepository) {
    this.roleWriteRepository = roleWriteRepository;
    this.roleReadRepository = roleReadRepository;
  }

  @Override
  @Transactional
  public void handler(UpdateRoleCommand command) {
    // Check if the role exists
    Role role =
        this.roleReadRepository
            .findById(command.id())
            .orElseThrow(() -> new NotFoundException("Role not found."));

    // Check if it global role
    if (role.getWorkspaceId().isEmpty()) {
      throw new NotFoundException("Role not found.");
    }

    // Check if the user is the creator
    role.getCreatedBy()
        .ifPresent(
            createdBy -> {
              if (!createdBy.equals(command.invokedBy())) {
                throw new NotFoundException("Role not found.");
              }
            });

    Instant now = Instant.now();

    // Check the updated data
    if (command.name() != null
        && !command.name().isBlank()
        && !command.name().equals(role.getName())) {
      role.rename(command.name(), now, command.invokedBy());
    }

    if (command.description() != null
        && !command.description().isBlank()
        && !command.description().equals(role.getDescription())) {
      role.changeDescription(command.description(), now, command.invokedBy());
    }

    // Save the updated role
    this.roleWriteRepository.save(role);
  }
}
