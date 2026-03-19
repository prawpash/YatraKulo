package com.ekapasha.auth_service.workspace.presentation.controller;

import com.ekapasha.auth_service.shared.domain.pagination.DomainPage;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPageRequest;
import com.ekapasha.auth_service.workspace.application.command.workspace.*;
import com.ekapasha.auth_service.workspace.application.command.workspacemember.*;
import com.ekapasha.auth_service.workspace.application.query.workspace.*;
import com.ekapasha.auth_service.workspace.application.query.workspacemember.*;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;
import com.ekapasha.auth_service.workspace.presentation.dto.*;
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

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
@Tag(name = "Workspaces", description = "Workspace related operations")
public class WorkspaceController {

  // Query Handlers
  private final ListWorkspacesByOwnerQueryHandler listWorkspacesByOwnerQueryHandler;
  private final GetWorkspaceByIdQueryHandler getWorkspaceByIdQueryHandler;
  private final GetDefaultWorkspaceByOwnerQueryHandler getDefaultWorkspaceByOwnerQueryHandler;

  // Command Handlers
  private final CreateWorkspaceCommandHandler createWorkspaceCommandHandler;
  private final UpdateWorkspaceCommandHandler updateWorkspaceCommandHandler;
  private final DeleteWorkspaceCommandHandler deleteWorkspaceCommandHandler;
  private final SetWorkspaceDefaultCommandHandler setWorkspaceDefaultCommandHandler;

  // Workspace Member System
  private final AddWorkspaceMemberCommandHandler addWorkspaceMemberCommandHandler;
  private final RemoveWorkspaceMemberCommandHandler removeWorkspaceMemberCommandHandler;
  private final UpdateWorkspaceMemberRoleCommandHandler updateWorkspaceMemberRoleCommandHandler;
  private final ListWorkspaceMembersByWorkspaceQueryHandler
      listWorkspaceMembersByWorkspaceQueryHandler;
  private final GetWorkspaceMemberByWorkspaceAndUserQueryHandler
      getWorkspaceMemberByWorkspaceAndUserQueryHandler;

  @Operation(summary = "Create workspace", description = "Create a new workspace")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ResponseEntity<Workspace> createWorkspace(
      @Valid @RequestBody CreateWorkspaceRequestDto request, @AuthenticationPrincipal Jwt jwt) {
    UUID ownerId = UUID.fromString(jwt.getSubject());
    UUID invokedBy = UUID.fromString(jwt.getSubject());

    Workspace workspace =
        createWorkspaceCommandHandler.handler(request.toCommand(ownerId, invokedBy));

    return ResponseEntity.created(URI.create("/workspaces/" + workspace.getId())).body(workspace);
  }

  @Operation(
      summary = "List workspaces",
      description = "Get all workspaces for current user with pagination")
  @GetMapping
  public ResponseEntity<DomainPage<Workspace>> listWorkspaces(
      @ParameterObject @Valid ListWorkspacesFilterDto filter, @AuthenticationPrincipal Jwt jwt) {
    UUID ownerId = UUID.fromString(jwt.getSubject());

    DomainPage<Workspace> workspaces =
        listWorkspacesByOwnerQueryHandler.handler(
            new ListWorkspacesByOwnerQuery(ownerId, filter.toPageRequest()));

    return ResponseEntity.ok(workspaces);
  }

  @Operation(summary = "Get workspace", description = "Get workspace by ID")
  @GetMapping("/{id}")
  public ResponseEntity<Workspace> getWorkspace(@PathVariable UUID id) {
    Workspace workspace = getWorkspaceByIdQueryHandler.handler(new GetWorkspaceByIdQuery(id));

    return ResponseEntity.ok(workspace);
  }

  @Operation(summary = "Update workspace", description = "Update workspace details")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PatchMapping("/{id}")
  public ResponseEntity<Void> updateWorkspace(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateWorkspaceRequestDto request,
      @AuthenticationPrincipal Jwt jwt) {
    UUID invokedBy = UUID.fromString(jwt.getSubject());

    updateWorkspaceCommandHandler.handler(request.toCommand(id, invokedBy));

    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Delete workspace", description = "Delete a workspace")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteWorkspace(
      @PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    UUID invokedBy = UUID.fromString(jwt.getSubject());

    deleteWorkspaceCommandHandler.handler(new DeleteWorkspaceCommand(id, invokedBy));

    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Set default", description = "Set or unset workspace as default")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PutMapping("/{id}/default")
  public ResponseEntity<Void> setDefault(
      @PathVariable UUID id,
      @RequestBody SetDefaultRequestDto request,
      @AuthenticationPrincipal Jwt jwt) {
    UUID invokedBy = UUID.fromString(jwt.getSubject());

    setWorkspaceDefaultCommandHandler.handler(request.toCommand(id, invokedBy));

    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Get default workspace", description = "Get user's default workspace")
  @GetMapping("/default")
  public ResponseEntity<Workspace> getDefaultWorkspace(@AuthenticationPrincipal Jwt jwt) {
    UUID ownerId = UUID.fromString(jwt.getSubject());

    Workspace workspace =
        getDefaultWorkspaceByOwnerQueryHandler.handler(
            new GetDefaultWorkspaceByOwnerQuery(ownerId));

    return ResponseEntity.ok(workspace);
  }

  // ==========================================
  // Workspace Member Endpoints
  // ==========================================

  @Operation(summary = "Add member", description = "Add a new member to the workspace")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{id}/members")
  public ResponseEntity<Void> addMember(
      @PathVariable UUID id,
      @Valid @RequestBody AddWorkspaceMemberRequestDto request,
      @AuthenticationPrincipal Jwt jwt) {
    UUID invokedBy = UUID.fromString(jwt.getSubject());

    addWorkspaceMemberCommandHandler.handler(request.toCommand(id, invokedBy));

    return ResponseEntity.created(URI.create("/workspaces/" + id + "/members/" + request.userId()))
        .build();
  }

  @Operation(
      summary = "List members",
      description = "Get all members of a workspace with pagination")
  @GetMapping("/{id}/members")
  public ResponseEntity<DomainPage<WorkspaceMember>> listMembers(
      @PathVariable UUID id, @ParameterObject @Valid DomainPageRequest pageRequest) {

    DomainPage<WorkspaceMember> members =
        listWorkspaceMembersByWorkspaceQueryHandler.handler(
            new ListWorkspaceMembersByWorkspaceQuery(id, pageRequest));

    return ResponseEntity.ok(members);
  }

  @Operation(summary = "Get member", description = "Get a specific workspace member by user ID")
  @GetMapping("/{id}/members/{userId}")
  public ResponseEntity<WorkspaceMember> getMember(
      @PathVariable UUID id, @PathVariable UUID userId) {

    WorkspaceMember member =
        getWorkspaceMemberByWorkspaceAndUserQueryHandler.handler(
            new GetWorkspaceMemberByWorkspaceAndUserQuery(id, userId));

    return ResponseEntity.ok(member);
  }

  @Operation(summary = "Update member role", description = "Update the role of a workspace member")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PatchMapping("/{id}/members/{userId}/role")
  public ResponseEntity<Void> updateMemberRole(
      @PathVariable UUID id,
      @PathVariable UUID userId,
      @Valid @RequestBody UpdateWorkspaceMemberRoleRequestDto request,
      @AuthenticationPrincipal Jwt jwt) {
    UUID invokedBy = UUID.fromString(jwt.getSubject());

    updateWorkspaceMemberRoleCommandHandler.handler(request.toCommand(id, userId, invokedBy));

    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Remove member", description = "Remove a member from the workspace")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}/members/{userId}")
  public ResponseEntity<Void> removeMember(
      @PathVariable UUID id, @PathVariable UUID userId, @AuthenticationPrincipal Jwt jwt) {
    UUID invokedBy = UUID.fromString(jwt.getSubject());

    removeWorkspaceMemberCommandHandler.handler(
        new RemoveWorkspaceMemberCommand(id, userId, invokedBy));

    return ResponseEntity.noContent().build();
  }
}
