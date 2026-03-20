package com.ekapasha.auth_service.role.presentation.controller;

import com.ekapasha.auth_service.role.application.query.permission.GetPermissionByCodeQuery;
import com.ekapasha.auth_service.role.application.query.permission.GetPermissionByCodeQueryHandler;
import com.ekapasha.auth_service.role.application.query.permission.ListPermissionsQueryHandler;
import com.ekapasha.auth_service.role.application.query.permission.SearchPermissionsQueryHandler;
import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.presentation.dto.permission.ListPermissionsFilterDto;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPage;
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
  private final SearchPermissionsQueryHandler searchPermissionsQueryHandler;

  public PermissionController(
      ListPermissionsQueryHandler listPermissionsQueryHandler,
      GetPermissionByCodeQueryHandler getPermissionByCodeQueryHandler,
      SearchPermissionsQueryHandler searchPermissionsQueryHandler) {
    this.listPermissionsQueryHandler = listPermissionsQueryHandler;
    this.getPermissionByCodeQueryHandler = getPermissionByCodeQueryHandler;
    this.searchPermissionsQueryHandler = searchPermissionsQueryHandler;
  }

  @Operation(summary = "List permissions", description = "Endpoint to get all permissions")
  @GetMapping
  public ResponseEntity<DomainPage<Permission>> listPermissions(
      @ParameterObject @Valid ListPermissionsFilterDto listPermissionsFilterDto) {
    if (listPermissionsFilterDto.search() != null && !listPermissionsFilterDto.search().isBlank()) {
      return ResponseEntity.ok(
          this.searchPermissionsQueryHandler.handler(
              listPermissionsFilterDto.toSearchPermissionsQuery()));
    }

    DomainPage<Permission> permissions =
        this.listPermissionsQueryHandler.handler(listPermissionsFilterDto.toListPermissionsQuery());

    return ResponseEntity.ok(permissions);
  }

  @Operation(
      summary = "Get permission by code",
      description = "Endpoint to get a permission by code")
  @GetMapping("/{code}")
  public ResponseEntity<Permission> getPermissionByCode(@PathVariable String code) {
    Permission permission =
        this.getPermissionByCodeQueryHandler.handler(new GetPermissionByCodeQuery(code));
    return ResponseEntity.ok(permission);
  }
}