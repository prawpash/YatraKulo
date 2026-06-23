package com.ekapasha.auth_service.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SchemaMigrationIntegrationTest extends PostgresTestSupport {
  @Autowired private JdbcTemplate jdbcTemplate;

  @Test
  void shouldApplyMigrationsAndSeedReferenceData() {
    Long permissionCount = jdbcTemplate.queryForObject("select count(*) from permission", Long.class);
    Long ownerRoleCount =
        jdbcTemplate.queryForObject(
            "select count(*) from role where name = 'Owner' and workspace_id is null", Long.class);
    String uuid =
        jdbcTemplate.queryForObject("select gen_random_uuid()::text", String.class);

    assertThat(permissionCount).isEqualTo(7L);
    assertThat(ownerRoleCount).isEqualTo(1L);
    assertThat(uuid).isNotBlank();
  }
}
