package com.ekapasha.auth_service.user.application.command.user;

import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.domain.repository.UserReadRepository;
import com.ekapasha.auth_service.user.domain.repository.UserWriteRepository;
import com.ekapasha.shared.exception.DuplicateDataException;
import com.ekapasha.shared.exception.NotFoundException;
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
class UpdateUserProfileCommandHandlerTest {

  @Mock private UserReadRepository userReadRepository;
  @Mock private UserWriteRepository userWriteRepository;
  @InjectMocks private UpdateUserProfileCommandHandler handler;

  @Test
  void shouldUpdateRequestedFields() {
    User user = user();
    when(userReadRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(userReadRepository.findByUsername("new-username")).thenReturn(Optional.empty());
    when(userReadRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(userWriteRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User result =
        handler.handler(
            new UpdateUserProfileCommand(
                USER_ID,
                "Jane Updated",
                "new-username",
                "new@example.com",
                "https://example.com/new.png",
                USER_ID));

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userWriteRepository).save(captor.capture());
    User saved = captor.getValue();
    assertThat(result).isSameAs(saved);
    assertThat(saved.getName()).isEqualTo("Jane Updated");
    assertThat(saved.getUsername()).isEqualTo("new-username");
    assertThat(saved.getEmail()).isEqualTo("new@example.com");
    assertThat(saved.getProfilePictureURL()).contains("https://example.com/new.png");
    assertThat(saved.getUpdatedAt()).isNotNull();
  }

  @Test
  void shouldRejectMissingOrMismatchedInvoker() {
    assertThatThrownBy(
            () -> handler.handler(new UpdateUserProfileCommand(USER_ID, "Jane", null, null, null, null)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("User not found.");

    assertThatThrownBy(
            () -> handler.handler(new UpdateUserProfileCommand(USER_ID, "Jane", null, null, null, OTHER_USER_ID)))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("User not found.");
  }

  @Test
  void shouldRejectDuplicateUsername() {
    when(userReadRepository.findById(USER_ID)).thenReturn(Optional.of(user()));
    when(userReadRepository.findByUsername("taken-username")).thenReturn(
        Optional.of(
            User.builder()
                .id(OTHER_USER_ID)
                .name("Other")
                .username("taken-username")
                .email("other@example.com")
                .hashedPassword("hashed")
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build()));

    assertThatThrownBy(
            () ->
                handler.handler(
                    new UpdateUserProfileCommand(
                        USER_ID, null, "taken-username", null, null, USER_ID)))
        .isInstanceOf(DuplicateDataException.class)
        .hasMessage("Username already taken.");
  }

  @Test
  void shouldRejectDuplicateEmail() {
    when(userReadRepository.findById(USER_ID)).thenReturn(Optional.of(user()));
    when(userReadRepository.findByEmail("taken@example.com")).thenReturn(
        Optional.of(
            User.builder()
                .id(OTHER_USER_ID)
                .name("Other")
                .username("other")
                .email("taken@example.com")
                .hashedPassword("hashed")
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build()));

    assertThatThrownBy(
            () ->
                handler.handler(
                    new UpdateUserProfileCommand(
                        USER_ID, null, null, "taken@example.com", null, USER_ID)))
        .isInstanceOf(DuplicateDataException.class)
        .hasMessage("Email already taken.");
  }
}
