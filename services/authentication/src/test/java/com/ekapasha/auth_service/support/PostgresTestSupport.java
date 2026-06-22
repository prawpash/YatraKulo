package com.ekapasha.auth_service.support;

import org.flywaydb.core.Flyway;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.nio.file.Path;

public abstract class PostgresTestSupport {
  protected static final Path MIGRATIONS_PATH =
      Path.of("..", "..", "infra", "db", "authentication", "migrations")
          .toAbsolutePath()
          .normalize();
  protected static final TestRsaKeys RSA_KEYS = TestRsaKeys.generate();

  @SuppressWarnings("resource")
  protected static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:17.5-alpine3.22")
          .withDatabaseName("auth")
          .withUsername("auth")
          .withPassword("auth");

  static {
    POSTGRES.start();
    migrateSchema();
  }

  @DynamicPropertySource
  static void registerCommonProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
    registry.add("app.rsa.key-id", RSA_KEYS::keyId);
    registry.add("app.rsa.public-key", RSA_KEYS::publicKey);
    registry.add("app.rsa.private-key", RSA_KEYS::privateKey);
  }

  private static void migrateSchema() {
    try {
      Flyway.configure()
          .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
          .locations("filesystem:" + MIGRATIONS_PATH)
          .load()
          .migrate();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to initialize Postgres test database", e);
    }
  }
}
