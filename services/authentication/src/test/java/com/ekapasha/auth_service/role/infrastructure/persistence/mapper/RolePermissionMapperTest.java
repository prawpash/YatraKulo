package com.ekapasha.auth_service.role.infrastructure.persistence.mapper;

import com.ekapasha.auth_service.role.domain.entity.RolePermission;
import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.infrastructure.persistence.entity.RolePermissionEntity;
import org.junit.jupiter.api.Test;

import static com.ekapasha.auth_service.role.RoleTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

class RolePermissionMapperTest {

  @Test
  void shouldMapEntityToDomainAndBack() {
    RolePermissionEntity entity =
        new RolePermissionEntity(ROLE_PERMISSION_ID, ROLE_ID, "workspace.update", CREATED_AT);

    RolePermission domain = RolePermissionMapper.toDomain(entity);

    assertThat(domain.getId()).isEqualTo(ROLE_PERMISSION_ID);
    assertThat(domain.getRoleId()).isEqualTo(ROLE_ID);
    assertThat(domain.getPermission()).isEqualTo(Permission.WORKSPACE_UPDATE);
    assertThat(domain.getAddedAt()).isEqualTo(CREATED_AT);

    RolePermissionEntity mappedBack = RolePermissionMapper.toEntity(domain);
    assertThat(mappedBack).isEqualTo(entity);
    assertThat(mappedBack.getPermissionCode()).isEqualTo("workspace.update");
  }
}
