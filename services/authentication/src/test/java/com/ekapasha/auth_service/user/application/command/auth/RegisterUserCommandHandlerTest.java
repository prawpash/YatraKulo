package com.ekapasha.auth_service.user.application.command.auth;

import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.domain.repository.UserReadRepository;
import com.ekapasha.auth_service.user.domain.repository.UserWriteRepository;
import com.ekapasha.auth_service.user.domain.service.PasswordService;
import com.ekapasha.shared.exception.DuplicateDataException;
import com.ekapasha.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.ekapasha.auth_service.user.UserTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserCommandHandlerTest {

  @Mock private UserWriteRepository userWriteRepository;
  @Mock private PasswordService passwordService;
  @Mock private UserReadRepository userReadRepository;
  @InjectMocks private RegisterUserCommandHandler handler;

  @Test
  void shouldRegisterUserWhenDataIsValid() {
    when(userReadRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());
    when(userReadRepository.findByUsername("jane")).thenReturn(Optional.empty());
    when(passwordService.hashPassword("password123")).thenReturn("hashed-password");
    when(userWriteRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User result =
        handler.handler(
            new RegisterUserCommand(
                "Jane Doe", "jane", "jane@example.com", "password123", "https://example.com/avatar.png"));

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userWriteRepository).save(captor.capture());
    User saved = captor.getValue();
    assertThat(result).isSameAs(saved);
    assertThat(saved.getName()).isEqualTo("Jane Doe");
    assertThat(saved.getUsername()).isEqualTo("jane");
    assertThat(saved.getEmail()).isEqualTo("jane@example.com");
    assertThat(saved.getHashedPassword()).isEqualTo("hashed-password");
    assertThat(saved.getProfilePictureURL()).contains("https://example.com/avatar.png");
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getUpdatedAt()).isNotNull();
    assertThat(saved.getUpdatedAt()).isAfterOrEqualTo(saved.getCreatedAt());
  }

  @Test
  void shouldRejectBlankFields() {
    assertThatThrownBy(
            () -> handler.handler(new RegisterUserCommand(" ", "jane", "jane@example.com", "password123", null)))
        .isInstanceOf(ValidationException.class)
        .satisfies(
            throwable -> {
              ValidationException exception = (ValidationException) throwable;
              assertThat(exception.getProperty()).isEqualTo("name");
            });

    assertThatThrownBy(
            () -> handler.handler(new RegisterUserCommand("Jane Doe", " ", "jane@example.com", "password123", null)))
        .isInstanceOf(ValidationException.class)
        .satisfies(
            throwable -> {
              ValidationException exception = (ValidationException) throwable;
              assertThat(exception.getProperty()).isEqualTo("username");
            });

    assertThatThrownBy(
            () -> handler.handler(new RegisterUserCommand("Jane Doe", "jane", " ", "password123", null)))
        .isInstanceOf(ValidationException.class)
        .satisfies(
            throwable -> {
              ValidationException exception = (ValidationException) throwable;
              assertThat(exception.getProperty()).isEqualTo("email");
            });

    assertThatThrownBy(
            () -> handler.handler(new RegisterUserCommand("Jane Doe", "jane", "jane@example.com", " ", null)))
        .isInstanceOf(ValidationException.class)
        .satisfies(
            throwable -> {
              ValidationException exception = (ValidationException) throwable;
              assertThat(exception.getProperty()).isEqualTo("password");
            });
  }

  @Test
  void shouldRejectDuplicateEmailOrUsername() {
    when(userReadRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user()));

    assertThatThrownBy(
            () -> handler.handler(new RegisterUserCommand("Jane Doe", "jane", "jane@example.com", "password123", null)))
        .isInstanceOf(DuplicateDataException.class)
        .hasMessage("email already exists.");

    when(userReadRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());
    when(userReadRepository.findByUsername("jane")).thenReturn(Optional.of(user()));

    assertThatThrownBy(
            () -> handler.handler(new RegisterUserCommand("Jane Doe", "jane", "jane@example.com", "password123", null)))
        .isInstanceOf(DuplicateDataException.class)
        .hasMessage("username already exists.");
  }
}
