package com.ekapasha.auth_service.user.application.command.auth;

import com.ekapasha.auth_service.shared.application.command.CommandHandler;
import com.ekapasha.auth_service.shared.domain.exception.DuplicateDataException;
import com.ekapasha.auth_service.shared.domain.exception.ValidationException;
import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.domain.repository.UserReadRepository;
import com.ekapasha.auth_service.user.domain.repository.UserWriteRepository;
import com.ekapasha.auth_service.user.domain.service.PasswordService;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class RegisterUserCommandHandler implements CommandHandler<RegisterUserCommand, User> {
  private final UserWriteRepository userWriteRepository;
  private final PasswordService passwordService;
  private final UserReadRepository userReadRepository;

  @Override
  public User handler(RegisterUserCommand command) {
    // validate data
    if (command.name() == null || command.name().isBlank()) {
      throw new ValidationException("name", "name must not be null or blank.");
    }

    if (command.username() == null || command.username().isBlank()) {
      throw new ValidationException("username", "username must not be null or blank.");
    }

    if (command.email() == null || command.email().isBlank()) {
      throw new ValidationException("email", "email must not be null or blank.");
    }

    if (command.password() == null || command.password().isBlank()) {
      throw new ValidationException("password", "password must not be null or blank.");
    }

    // check the existence of the data
    this.userReadRepository
        .findByEmail(command.email())
        .ifPresent(
            user -> {
              throw new DuplicateDataException("email already exists.");
            });

    this.userReadRepository
        .findByUsername(command.username())
        .ifPresent(
            user -> {
              throw new DuplicateDataException("username already exists.");
            });

    User user =
        User.builder()
            .id(UUID.randomUUID())
            .name(command.name())
            .username(command.username())
            .email(command.email())
            .hashedPassword(this.passwordService.hashPassword(command.password()))
            .profilePictureURL(command.profilePictureURL())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    this.userWriteRepository.save(user);

    return user;
  }
}
