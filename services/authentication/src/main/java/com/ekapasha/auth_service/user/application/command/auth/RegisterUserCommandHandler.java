package com.ekapasha.auth_service.user.application.command.auth;

import com.ekapasha.shared.cqrs.CommandHandler;
import com.ekapasha.shared.exception.DuplicateDataException;
import com.ekapasha.shared.exception.ValidationException;
import com.ekapasha.shared.logging.AppLogger;
import com.ekapasha.shared.logging.LogEvent;
import com.ekapasha.auth_service.shared.logging.AuthLogEvent;
import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.domain.repository.UserReadRepository;
import com.ekapasha.auth_service.user.domain.repository.UserWriteRepository;
import com.ekapasha.auth_service.user.domain.service.PasswordService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class RegisterUserCommandHandler implements CommandHandler<RegisterUserCommand, User> {
  private final UserWriteRepository userWriteRepository;
  private final PasswordService passwordService;
  private final UserReadRepository userReadRepository;
  private final MeterRegistry meterRegistry;
  private static final AppLogger logger = new AppLogger(RegisterUserCommandHandler.class);

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

      this.meterRegistry.counter("auth.user.registration.count").increment();

      logger.info(
          LogEvent.builder("User registered successfully: " + user.getUsername())
              .eventName(AuthLogEvent.USER_REGISTERED_SUCCESS)
              .metadata("user.id", user.getId().toString())
              .metadata("user.name", user.getUsername())
              .build());

      return user;
    } catch (Exception e) {
      if (e instanceof ValidationException || e instanceof DuplicateDataException) {
        logger.warn(
            LogEvent.builder("User registration failed: " + e.getMessage())
                .eventName(AuthLogEvent.USER_REGISTER_FAILED)
                .metadata("user.name", command.username())
                .error(e)
                .build());
      } else {
        logger.error(
            LogEvent.builder("User registration failed with system error: " + e.getMessage())
                .eventName(AuthLogEvent.USER_REGISTER_FAILED)
                .metadata("user.name", command.username())
                .error(e)
                .build());
      }
      throw e;
    }
  }
}
