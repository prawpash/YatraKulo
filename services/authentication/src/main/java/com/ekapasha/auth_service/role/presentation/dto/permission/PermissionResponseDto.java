package com.ekapasha.auth_service.role.presentation.dto.permission;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import io.swagger.v3.oas.annotations.media.Schema;

public record PermissionResponseDto(
    @Schema(description = "Permission code", example = "WORKSPACE_WRITE")
    String code,

    @Schema(description = "Permission description", example = "Ability to edit workspace settings")
    String description
) {
  public static PermissionResponseDto from(Permission permission) {
    return new PermissionResponseDto(permission.getCode(), permission.getDescription());
  }
}
