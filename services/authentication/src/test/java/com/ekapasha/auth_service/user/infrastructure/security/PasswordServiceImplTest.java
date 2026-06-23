package com.ekapasha.auth_service.user.infrastructure.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordServiceImplTest {

  private final PasswordServiceImpl service = new PasswordServiceImpl();

  @Test
  void shouldHashAndVerifyPassword() {
    String hash = service.hashPassword("password123");

    assertThat(hash).isNotBlank();
    assertThat(service.verifyPassword("password123", hash)).isTrue();
    assertThat(service.verifyPassword("wrong", hash)).isFalse();

    // Make sure the salt is working
    String hash2 = service.hashPassword("password123");
    assertThat(hash2).isNotEqualTo(hash);
    assertThat(service.verifyPassword("password123", hash2)).isTrue();
  }

  @Test
  void shouldRejectInvalidInputs() {
    assertThatThrownBy(() -> service.hashPassword(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("password must not be null");
    assertThatThrownBy(() -> service.hashPassword(" "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("password must not be blank");

    assertThat(service.verifyPassword(null, "hash")).isFalse();
    assertThat(service.verifyPassword(" ", "hash")).isFalse();
    assertThat(service.verifyPassword("password", null)).isFalse();
    assertThat(service.verifyPassword("password", " ")).isFalse();
  }
}
