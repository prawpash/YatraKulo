package com.ekapasha.auth_service.role.presentation.controller;

import com.ekapasha.auth_service.role.application.command.role.CreateRoleCommandHandler;
import com.ekapasha.auth_service.role.application.command.role.UpdateRoleCommandHandler;
import com.ekapasha.auth_service.role.application.command.role.UpdateRolePermissionsCommandHandler;
import com.ekapasha.auth_service.role.application.query.role.GetRoleByIdQuery;
import com.ekapasha.auth_service.role.application.query.role.GetRoleByIdQueryHandler;
import com.ekapasha.auth_service.role.application.query.role.ListRolesQueryHandler;
import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.presentation.dto.role.CreateRoleRequestDto;
import com.ekapasha.auth_service.role.presentation.dto.role.ListRolesFilterDto;
import com.ekapasha.auth_service.role.presentation.dto.role.UpdateRoleRequestDto;
import com.ekapasha.auth_service.role.presentation.dto.role.UpdateRolePermissionsRequestDto;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/roles")
@Tag(name = "Roles", description = "Roles related operations")
public class RoleController {
  //  Query
  private final ListRolesQueryHandler listRolesQueryHandler;
  private final GetRoleByIdQueryHandler getRoleByIdQueryHandler;

  //  Command
  private final CreateRoleCommandHandler createRoleCommandHandler;
  private final UpdateRoleCommandHandler updateRoleCommandHandler;
  private final UpdateRolePermissionsCommandHandler updateRolePermissionsCommandHandler;

  @Operation(summary = "List roles", description = "Endpoint to get all roles by workspace")
  @GetMapping
  public ResponseEntity<DomainPage<Role>> listRoles(
      @ParameterObject @Valid ListRolesFilterDto listRolesFilterDto) {
    DomainPage<Role> roles =
        this.listRolesQueryHandler.handler(listRolesFilterDto.toListRolesQuery());

    return ResponseEntity.ok(roles);
  }

  @Operation(summary = "Create role", description = "Endpoint to create a role in a workspace")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ResponseEntity<Role> createRole(
      @Valid @RequestBody CreateRoleRequestDto createRoleRequestDto,
      @AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    Role role =
        this.createRoleCommandHandler.handler(createRoleRequestDto.toCreateRoleCommand(userId));

    return ResponseEntity.created(URI.create("/roles" + role.getId())).body(role);
  }

  @Operation(summary = "Get role by id", description = "Endpoint to get a role by id")
  @GetMapping("/{id}")
  public ResponseEntity<Role> getRoleById(@PathVariable UUID id) {
    Role role = this.getRoleByIdQueryHandler.handler(new GetRoleByIdQuery(id));

    return ResponseEntity.ok(role);
  }

  @Operation(summary = "Update role", description = "Endpoint to update a role")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PatchMapping("/{id}")
  public ResponseEntity<Void> updateRole(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateRoleRequestDto updateRoleRequestDto,
      @AuthenticationPrincipal Jwt jwt
  ) {
    UUID userId = UUID.fromString(jwt.getSubject());

    this.updateRoleCommandHandler.handler(updateRoleRequestDto.toUpdateRoleCommand(id, userId));

    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Update role permissions",
      description = "Bulk assign or revoke permissions for a role")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PatchMapping("/{id}/permissions")
  public ResponseEntity<Void> updateRolePermissions(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateRolePermissionsRequestDto updateRolePermissionsRequestDto,
      @AuthenticationPrincipal Jwt jwt
  ) {
    UUID userId = UUID.fromString(jwt.getSubject());

    this.updateRolePermissionsCommandHandler.handler(
        updateRolePermissionsRequestDto.toCommand(id, userId));

    return ResponseEntity.noContent().build();
  }
}
