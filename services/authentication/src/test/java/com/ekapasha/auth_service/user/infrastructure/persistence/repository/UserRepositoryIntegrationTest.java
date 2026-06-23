package com.ekapasha.auth_service.user.infrastructure.persistence.repository;

import com.ekapasha.auth_service.support.PostgresTestSupport;
import com.ekapasha.auth_service.user.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserRepositoryIntegrationTest extends PostgresTestSupport {
  @Autowired private JPAUserRepository userRepository;

  @Test
  void shouldPersistAndFindUserByUsernameAndEmail() {
    UserEntity entity =
        new UserEntity(
            UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
            "Jane Doe",
            "jane",
            "jane@example.com",
            "$2a$10$012345678901234567890uGx9X9s1T7vY7kD2e1l7w6FJ9T7k0u1K",
            "https://example.com/avatar.png",
            java.time.Instant.parse("2026-03-01T00:00:00Z"),
            java.time.Instant.parse("2026-03-02T00:00:00Z"));

    userRepository.saveAndFlush(entity);

    assertThat(userRepository.findByUsername("jane")).hasValueSatisfying(user -> {
      assertThat(user.getId()).isEqualTo(entity.getId());
      assertThat(user.getEmail()).isEqualTo("jane@example.com");
    });
    assertThat(userRepository.findByEmail("jane@example.com")).hasValueSatisfying(user ->
        assertThat(user.getUsername()).isEqualTo("jane"));
  }

  @Test
  void shouldRejectDuplicateUsernameAndEmail() {
    UserEntity first =
        new UserEntity(
            UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
            "Jane Doe",
            "jane",
            "jane@example.com",
            "$2a$10$012345678901234567890uGx9X9s1T7vY7kD2e1l7w6FJ9T7k0u1K",
            null,
            java.time.Instant.parse("2026-03-01T00:00:00Z"),
            java.time.Instant.parse("2026-03-02T00:00:00Z"));
    UserEntity duplicate =
        new UserEntity(
            UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"),
            "Jane Roe",
            "jane",
            "jane@example.com",
            "$2a$10$012345678901234567890uGx9X9s1T7vY7kD2e1l7w6FJ9T7k0u1K",
            null,
            java.time.Instant.parse("2026-03-01T00:00:00Z"),
            java.time.Instant.parse("2026-03-02T00:00:00Z"));

    userRepository.saveAndFlush(first);

    assertThatThrownBy(() -> userRepository.saveAndFlush(duplicate))
        .isInstanceOf(DataIntegrityViolationException.class);
  }
}
