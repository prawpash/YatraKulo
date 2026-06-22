package com.ekapasha.auth_service.user.application.command.user;

import com.ekapasha.shared.cqrs.CommandHandler;
import com.ekapasha.shared.exception.DuplicateDataException;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.logging.AppLogger;
import com.ekapasha.shared.logging.LogEvent;
import com.ekapasha.auth_service.logging.AuthLogEvent;
import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.domain.repository.UserReadRepository;
import com.ekapasha.auth_service.user.domain.repository.UserWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
public class UpdateUserProfileCommandHandler
    implements CommandHandler<UpdateUserProfileCommand, User> {
  private final UserReadRepository userReadRepository;
  private final UserWriteRepository userWriteRepository;
  private final AppLogger logger = new AppLogger(UpdateUserProfileCommandHandler.class);

  @Override
  @Transactional
  public User handler(UpdateUserProfileCommand command) {
    try {
      // Prevent other user to change the data that their not own
      if (command.invokedBy() == null) {
        throw new NotFoundException("User not found.");
      }

      if (!command.invokedBy().equals(command.id())) {
        throw new NotFoundException("User not found.");
      }

      // Check the existence of the data
      User user =
          this.userReadRepository
              .findById(command.id())
              .orElseThrow(() -> new NotFoundException("User not found."));

      // Check if user change the name
      if (command.name() != null && !command.name().isBlank()) {
        user.rename(command.name(), Instant.now(), command.invokedBy());
      }

      // Check if user change the username
      if (command.username() != null && !command.username().isBlank()) {
        // Check if username is already taken
        this.userReadRepository
            .findByUsername(command.username())
            .filter(item -> !item.getId().equals(command.id()))
            .ifPresent(
                item -> {
                  throw new DuplicateDataException("Username already taken.");
                });

        user.changeUsername(command.username(), Instant.now(), command.invokedBy());
      }

      // Check if user change the email
      if (command.email() != null && !command.email().isBlank()) {
        // Check if email is already taken
        this.userReadRepository
            .findByEmail(command.email())
            .filter(item -> !item.getId().equals(command.id()))
            .ifPresent(
                item -> {
                  throw new DuplicateDataException("Email already taken.");
                });

        user.changeEmail(command.email(), Instant.now(), command.invokedBy());
      }

      // Check if user change the profile picture URL
      if (command.profilePictureURL() != null && !command.profilePictureURL().isBlank()) {
        user.changeProfilePictureURL(
            command.profilePictureURL(), Instant.now(), command.invokedBy());
      }

      this.userWriteRepository.save(user);

      this.logger.info(
          LogEvent.builder("User profile updated successfully: " + user.getId())
              .eventName(AuthLogEvent.USER_PROFILE_UPDATED)
              .metadata("user.id", user.getId().toString())
              .build());

      return user;
    } catch (Exception e) {
      String userId = command.id() != null ? command.id().toString() : "null";

      if (e instanceof NotFoundException || e instanceof DuplicateDataException) {
        this.logger.warn(
            LogEvent.builder("User profile update failed: " + e.getMessage())
                .eventName(AuthLogEvent.USER_PROFILE_UPDATE_FAILED)
                .metadata("user.id", userId)
                .error(e)
                .build());
      } else {
        this.logger.error(
            LogEvent.builder("User profile update failed with system error: " + e.getMessage())
                .eventName(AuthLogEvent.USER_PROFILE_UPDATE_FAILED)
                .metadata("user.id", userId)
                .error(e)
                .build());
      }
      throw e;
    }
  }
}
