package com.ekapasha.auth_service.role.presentation.controller;

import com.ekapasha.auth_service.role.application.command.role.CreateRoleCommandHandler;
import com.ekapasha.auth_service.role.application.command.role.UpdateRoleCommandHandler;
import com.ekapasha.auth_service.role.application.query.role.GetRoleByIdQuery;
import com.ekapasha.auth_service.role.application.query.role.GetRoleByIdQueryHandler;
import com.ekapasha.auth_service.role.application.query.role.ListRolesQueryHandler;
import com.ekapasha.auth_service.role.application.query.role.SearchRolesQueryHandler;
import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.presentation.dto.role.CreateRoleRequestDto;
import com.ekapasha.auth_service.role.presentation.dto.role.ListRolesFilterDto;
import com.ekapasha.auth_service.role.presentation.dto.role.UpdateRoleRequestDto;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  private final SearchRolesQueryHandler searchRolesQueryHandler;

  //  Command
  private final CreateRoleCommandHandler createRoleCommandHandler;
  private final UpdateRoleCommandHandler updateRoleCommandHandler;

  @Operation(summary = "List roles", description = "Endpoint to get all roles by workspace")
  @GetMapping
  public ResponseEntity<DomainPage<Role>> listRoles(
      @ParameterObject @Valid ListRolesFilterDto listRolesFilterDto) {
    if (listRolesFilterDto.search() != null && !listRolesFilterDto.search().isBlank()) {
      return ResponseEntity.ok(
          this.searchRolesQueryHandler.handler(listRolesFilterDto.toSearchRolesQuery()));
    }

    DomainPage<Role> roles =
        this.listRolesQueryHandler.handler(listRolesFilterDto.toListRolesQuery());

    return ResponseEntity.ok(roles);
  }

  @Operation(summary = "Create role", description = "Endpoint to create a role in a workspace")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ResponseEntity<Role> createRole(
      @Valid @RequestBody CreateRoleRequestDto createRoleRequestDto) {
    Role role =
        this.createRoleCommandHandler.handler(createRoleRequestDto.toCreateRoleCommand(null));

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
      @PathVariable UUID id, @Valid @RequestBody UpdateRoleRequestDto updateRoleRequestDto) {
    this.updateRoleCommandHandler.handler(updateRoleRequestDto.toUpdateRoleCommand(id, null));

    return ResponseEntity.noContent().build();
  }
}
