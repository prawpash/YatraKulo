package com.ekapasha.auth_service.role.application.command.role;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.repository.RoleReadRepository;
import com.ekapasha.auth_service.role.domain.repository.RoleWriteRepository;
import com.ekapasha.shared.cqrs.VoidCommandHandler;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.logging.AppLogger;
import com.ekapasha.shared.logging.LogEvent;
import com.ekapasha.auth_service.logging.AuthLogEvent;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public class UpdateRoleCommandHandler implements VoidCommandHandler<UpdateRoleCommand> {
  private final RoleWriteRepository roleWriteRepository;
  private final RoleReadRepository roleReadRepository;
  private final AppLogger logger = new AppLogger(UpdateRoleCommandHandler.class);

  public UpdateRoleCommandHandler(
      RoleWriteRepository roleWriteRepository, RoleReadRepository roleReadRepository) {
    this.roleWriteRepository = roleWriteRepository;
    this.roleReadRepository = roleReadRepository;
  }

  @Override
  @Transactional
  public void handler(UpdateRoleCommand command) {
    try {
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

      this.logger.info(
          LogEvent.builder("Role updated successfully: " + role.getName())
              .eventName(AuthLogEvent.ROLE_UPDATED)
              .metadata("role.id", role.getId().toString())
              .metadata("role.name", role.getName())
              .build());
    } catch (Exception e) {
      String roleId = command.id() != null ? command.id().toString() : "null";
      if (e instanceof NotFoundException) {
        this.logger.warn(
            LogEvent.builder("Role update failed: " + e.getMessage())
                .eventName(AuthLogEvent.ROLE_OPERATION_FAILED)
                .metadata("role.id", roleId)
                .error(e)
                .build());
      } else {
        this.logger.error(
            LogEvent.builder("Role update failed with system error: " + e.getMessage())
                .eventName(AuthLogEvent.ROLE_OPERATION_FAILED)
                .metadata("role.id", roleId)
                .error(e)
                .build());
      }
      throw e;
    }
  }
}
