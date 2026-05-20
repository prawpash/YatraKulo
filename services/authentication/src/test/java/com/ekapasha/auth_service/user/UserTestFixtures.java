package com.ekapasha.auth_service.user;

import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.infrastructure.persistence.entity.UserEntity;

import java.time.Instant;
import java.util.UUID;

public final class UserTestFixtures {
  public static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  public static final UUID OTHER_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111112");
  public static final Instant CREATED_AT = Instant.parse("2026-03-01T00:00:00Z");
  public static final Instant UPDATED_AT = Instant.parse("2026-03-02T00:00:00Z");

  private UserTestFixtures() {}

  public static User user() {
    return User.builder()
        .id(USER_ID)
        .name("Jane Doe")
        .username("jane")
        .email("jane@example.com")
        .hashedPassword("$2a$10$012345678901234567890uGx9X9s1T7vY7kD2e1l7w6FJ9T7k0u1K")
        .profilePictureURL("https://example.com/avatar.png")
        .createdAt(CREATED_AT)
        .updatedAt(UPDATED_AT)
        .build();
  }

  public static User userWithoutProfilePicture() {
    return User.builder()
        .id(USER_ID)
        .name("Jane Doe")
        .username("jane")
        .email("jane@example.com")
        .hashedPassword("$2a$10$012345678901234567890uGx9X9s1T7vY7kD2e1l7w6FJ9T7k0u1K")
        .profilePictureURL(null)
        .createdAt(CREATED_AT)
        .updatedAt(UPDATED_AT)
        .build();
  }

  public static UserEntity userEntity() {
    return new UserEntity(
        USER_ID,
        "Jane Doe",
        "jane",
        "jane@example.com",
        "$2a$10$012345678901234567890uGx9X9s1T7vY7kD2e1l7w6FJ9T7k0u1K",
        "https://example.com/avatar.png",
        CREATED_AT,
        UPDATED_AT);
  }
}
