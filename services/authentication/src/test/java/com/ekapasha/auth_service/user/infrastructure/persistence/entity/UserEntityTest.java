package com.ekapasha.auth_service.user.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import static com.ekapasha.auth_service.user.UserTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {

  @Test
  void shouldUseIdBasedEquality() {
    UserEntity left =
        new UserEntity(USER_ID, "Jane Doe", "jane", "jane@example.com", "hash", null, CREATED_AT, UPDATED_AT);
    UserEntity right =
        new UserEntity(USER_ID, "Other", "other", "other@example.com", "other-hash", "https://example.com", CREATED_AT, UPDATED_AT);

    assertThat(left).isEqualTo(right);
    assertThat(left).hasSameHashCodeAs(right);
  }
}
