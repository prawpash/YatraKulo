package com.ekapasha.auth_service.role.domain.entity;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import lombok.*;

import java.time.Instant;
import java.util.*;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RolePermission {
  @EqualsAndHashCode.Include
  private final UUID id;

  private final UUID roleId;

  private final Permission permission;

  private final Instant addedAt;

  @Builder
  private RolePermission(
      @NonNull UUID id,
      @NonNull UUID roleId,
      @NonNull Permission permission,
      @NonNull Instant addedAt
  ) {
    this.id = Objects.requireNonNull(id, "id");
    this.roleId = Objects.requireNonNull(roleId, "roleId");
    this.permission = Objects.requireNonNull(permission, "permissions");

    this.addedAt = Objects.requireNonNull(addedAt, "addedAt");
  }
}
