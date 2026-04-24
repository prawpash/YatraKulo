package com.ekapasha.auth_service.role.presentation.controller;

import com.ekapasha.auth_service.role.application.query.permission.GetPermissionByCodeQuery;
import com.ekapasha.auth_service.role.application.query.permission.GetPermissionByCodeQueryHandler;
import com.ekapasha.auth_service.role.application.query.permission.ListPermissionsQueryHandler;
import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.presentation.dto.permission.ListPermissionsFilterDto;
import com.ekapasha.auth_service.role.presentation.dto.permission.PermissionResponseDto;
import com.ekapasha.shared.pagination.DomainPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/permissions")
@Tag(name = "Permissions", description = "Permissions related operations")
public class PermissionController {

  //  Query
  private final ListPermissionsQueryHandler listPermissionsQueryHandler;
  private final GetPermissionByCodeQueryHandler getPermissionByCodeQueryHandler;
  public PermissionController(
      ListPermissionsQueryHandler listPermissionsQueryHandler,
      GetPermissionByCodeQueryHandler getPermissionByCodeQueryHandler) {
    this.listPermissionsQueryHandler = listPermissionsQueryHandler;
    this.getPermissionByCodeQueryHandler = getPermissionByCodeQueryHandler;
  }

  @Operation(summary = "List permissions", description = "Endpoint to get all permissions")
  @GetMapping
  public ResponseEntity<DomainPage<PermissionResponseDto>> listPermissions(
      @ParameterObject @Valid ListPermissionsFilterDto listPermissionsFilterDto) {
    DomainPage<PermissionResponseDto> permissions =
        this.listPermissionsQueryHandler
            .handler(listPermissionsFilterDto.toListPermissionsQuery())
            .map(PermissionResponseDto::from);

    return ResponseEntity.ok(permissions);
  }

  @Operation(
      summary = "Get permission by code",
      description = "Endpoint to get a permission by code")
  @GetMapping("/{code}")
  public ResponseEntity<PermissionResponseDto> getPermissionByCode(@PathVariable String code) {
    Permission permission =
        this.getPermissionByCodeQueryHandler.handler(new GetPermissionByCodeQuery(code));

    return ResponseEntity.ok(PermissionResponseDto.from(permission));
  }
}
