package com.ekapasha.auth_service.role.presentation.dto.permission;

import com.ekapasha.auth_service.role.domain.enums.Permission;

public record PermissionResponseDto(
    String code,
    String description
) {
  public static PermissionResponseDto from(Permission permission) {
    return new PermissionResponseDto(permission.getCode(), permission.getDescription());
  }
}
