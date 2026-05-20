package com.ekapasha.auth_service.role.infrastructure.persistence.mapper;

import com.ekapasha.auth_service.role.RoleTestFixtures;
import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.infrastructure.persistence.entity.RoleEntity;
import org.junit.jupiter.api.Test;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

class RoleMapperTest {

  @Test
  void shouldMapEntityToDomainAndBack() {
    RoleEntity entity =
        new RoleEntity(
            ROLE_ID,
            WORKSPACE_ID,
            "Admin",
            "Admin role",
            CREATED_AT,
            UPDATED_AT,
            DELETED_AT,
            CREATED_BY,
            UPDATED_BY,
            UPDATED_BY);

    Role domain = RoleMapper.toDomain(entity);

    assertThat(domain.getId()).isEqualTo(ROLE_ID);
    assertThat(domain.getWorkspaceId()).contains(WORKSPACE_ID);
    assertThat(domain.getName()).isEqualTo("Admin");
    assertThat(domain.getDescription()).isEqualTo("Admin role");
    assertThat(domain.getDeletedAt()).contains(DELETED_AT);
    assertThat(domain.getCreatedBy()).contains(CREATED_BY);
    assertThat(domain.getUpdatedBy()).contains(UPDATED_BY);
    assertThat(domain.getDeletedBy()).contains(UPDATED_BY);

    RoleEntity mappedBack = RoleMapper.toEntity(domain);
    assertThat(mappedBack).isEqualTo(entity);
    assertThat(mappedBack.getWorkspaceId()).isEqualTo(WORKSPACE_ID);
    assertThat(mappedBack.getDeletedAt()).isEqualTo(DELETED_AT);
  }

  @Test
  void shouldPreserveGlobalRoleNulls() {
    Role domain = globalRole();

    RoleEntity entity = RoleMapper.toEntity(domain);

    assertThat(entity.getWorkspaceId()).isNull();
    assertThat(entity.getDeletedAt()).isNull();
    assertThat(entity.getDeletedBy()).isNull();
  }
}
