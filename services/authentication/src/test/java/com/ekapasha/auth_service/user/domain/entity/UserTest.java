package com.ekapasha.auth_service.user.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.ekapasha.auth_service.user.UserTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {
  private static final Instant LATER = Instant.parse("2026-03-03T00:00:00Z");

  @Test
  void shouldUpdateProfileFields() {
    User user = user();

    user.rename("Jane Updated", LATER, OTHER_USER_ID);
    user.changeUsername("jane-updated", LATER, OTHER_USER_ID);
    user.changeEmail("jane-updated@example.com", LATER, OTHER_USER_ID);
    user.changePassword("new-hash", LATER, OTHER_USER_ID);
    user.changeProfilePictureURL("https://example.com/new.png", LATER, OTHER_USER_ID);

    assertThat(user.getName()).isEqualTo("Jane Updated");
    assertThat(user.getUsername()).isEqualTo("jane-updated");
    assertThat(user.getEmail()).isEqualTo("jane-updated@example.com");
    assertThat(user.getHashedPassword()).isEqualTo("new-hash");
    assertThat(user.getProfilePictureURL()).contains("https://example.com/new.png");
    assertThat(user.getUpdatedAt()).isEqualTo(LATER);
  }

  @Test
  void shouldExposeOptionalProfilePicture() {
    User user = userWithoutProfilePicture();

    assertThat(user.getProfilePictureURL()).isEmpty();
  }

  @Test
  void shouldRejectBlankConstructorFields() {
    assertThatThrownBy(
            () ->
                User.builder()
                    .id(USER_ID)
                    .name(" ")
                    .username("jane")
                    .email("jane@example.com")
                    .hashedPassword("hash")
                    .createdAt(CREATED_AT)
                    .updatedAt(UPDATED_AT)
                    .build())
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldRejectBlankMutationValues() {
    User user = user();

    assertThatThrownBy(() -> user.rename(" ", Instant.now(), OTHER_USER_ID))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> user.changeUsername(" ", Instant.now(), OTHER_USER_ID))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> user.changeEmail(" ", Instant.now(), OTHER_USER_ID))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> user.changePassword(" ", Instant.now(), OTHER_USER_ID))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
