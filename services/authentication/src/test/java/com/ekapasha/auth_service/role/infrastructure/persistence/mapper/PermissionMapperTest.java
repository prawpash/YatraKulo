package com.ekapasha.auth_service.role.infrastructure.persistence.mapper;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.infrastructure.persistence.entity.PermissionEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionMapperTest {

  @Test
  void shouldMapToDomainAndBack() {
    PermissionEntity entity = new PermissionEntity("workspace.update", "Update workspace");

    Permission domain = PermissionMapper.toDomain(entity);
    assertThat(domain).isEqualTo(Permission.WORKSPACE_UPDATE);

    PermissionEntity mappedBack = PermissionMapper.toEntity(domain);
    assertThat(mappedBack.getCode()).isEqualTo("workspace.update");
    assertThat(mappedBack.getDescription()).isEqualTo("Update workspace");
  }
}
