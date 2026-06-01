package com.ekapasha.auth_service.user.application.command.auth;

import com.ekapasha.shared.cqrs.CommandHandler;
import com.ekapasha.shared.exception.DuplicateDataException;
import com.ekapasha.shared.exception.ValidationException;
import com.ekapasha.shared.logging.AppLogger;
import com.ekapasha.shared.logging.LogEvent;
import com.ekapasha.auth_service.logging.AuthLogEvent;
import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.domain.repository.UserReadRepository;
import com.ekapasha.auth_service.user.domain.repository.UserWriteRepository;
import com.ekapasha.auth_service.user.domain.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class RegisterUserCommandHandler implements CommandHandler<RegisterUserCommand, User> {
  private final UserWriteRepository userWriteRepository;
  private final PasswordService passwordService;
  private final UserReadRepository userReadRepository;
  private final AppLogger logger = new AppLogger(RegisterUserCommandHandler.class);

  @Override
  @Transactional
  public User handler(RegisterUserCommand command) {
    try {
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

      this.logger.info(
          LogEvent.builder("User registered successfully: " + user.getUsername())
              .eventName(AuthLogEvent.USER_REGISTERED_SUCCESS)
              .metadata("userId", user.getId().toString())
              .metadata("username", user.getUsername())
              .metadata("email", user.getEmail())
              .build());

      return user;
    } catch (Exception e) {
      if (e instanceof ValidationException || e instanceof DuplicateDataException) {
        this.logger.warn(
            LogEvent.builder("User registration failed: " + e.getMessage())
                .eventName(AuthLogEvent.USER_REGISTER_FAILED)
                .metadata("username", command.username())
                .metadata("email", command.email())
                .error(e)
                .build());
      } else {
        this.logger.error(
            LogEvent.builder("User registration failed with system error: " + e.getMessage())
                .eventName(AuthLogEvent.USER_REGISTER_FAILED)
                .metadata("username", command.username())
                .metadata("email", command.email())
                .error(e)
                .build());
      }
      throw e;
    }
  }
}
