package com.ekapasha.auth_service.workspace;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.domain.entity.RolePermission;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;
import com.ekapasha.shared.pagination.DomainPage;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class WorkspaceTestFixtures {
  public static final UUID WORKSPACE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  public static final UUID WORKSPACE_ID_2 = UUID.fromString("11111111-1111-1111-1111-111111111112");
  public static final UUID OWNER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
  public static final UUID OTHER_USER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
  public static final UUID ROLE_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
  public static final UUID ROLE_ID_2 = UUID.fromString("44444444-4444-4444-4444-444444444445");
  public static final UUID MEMBER_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
  public static final UUID MEMBER_ID_2 = UUID.fromString("55555555-5555-5555-5555-555555555556");
  public static final UUID CREATED_BY = UUID.fromString("66666666-6666-6666-6666-666666666666");
  public static final UUID UPDATED_BY = UUID.fromString("77777777-7777-7777-7777-777777777777");
  public static final Instant CREATED_AT = Instant.parse("2026-02-01T00:00:00Z");
  public static final Instant UPDATED_AT = Instant.parse("2026-02-02T00:00:00Z");
  public static final Instant LATER = Instant.parse("2026-02-03T00:00:00Z");

  private WorkspaceTestFixtures() {}

  public static Workspace workspace() {
    return Workspace.builder()
        .id(WORKSPACE_ID)
        .name("Workspace")
        .description("Workspace description")
        .ownerId(OWNER_ID)
        .isDefault(false)
        .createdAt(CREATED_AT)
        .updatedAt(UPDATED_AT)
        .build();
  }

  public static Workspace workspace(String name, String description) {
    return Workspace.builder()
        .id(WORKSPACE_ID)
        .name(name)
        .description(description)
        .ownerId(OWNER_ID)
        .isDefault(false)
        .createdAt(CREATED_AT)
        .updatedAt(UPDATED_AT)
        .build();
  }

  public static Workspace defaultWorkspace() {
    return Workspace.builder()
        .id(WORKSPACE_ID_2)
        .name("Default")
        .description("Default workspace")
        .ownerId(OWNER_ID)
        .isDefault(true)
        .createdAt(CREATED_AT)
        .updatedAt(UPDATED_AT)
        .build();
  }

  public static Workspace workspaceOwnedBy(UUID ownerId) {
    return Workspace.builder()
        .id(WORKSPACE_ID)
        .name("Workspace")
        .description("Workspace description")
        .ownerId(ownerId)
        .isDefault(false)
        .createdAt(CREATED_AT)
        .updatedAt(UPDATED_AT)
        .build();
  }

  public static WorkspaceMember workspaceMember() {
    return WorkspaceMember.builder()
        .id(MEMBER_ID)
        .workspaceId(WORKSPACE_ID)
        .userId(OTHER_USER_ID)
        .roleId(ROLE_ID)
        .addedAt(CREATED_AT)
        .updatedAt(UPDATED_AT)
        .addedBy(OWNER_ID)
        .updatedBy(OWNER_ID)
        .build();
  }

  public static WorkspaceMember ownerMember() {
    return WorkspaceMember.builder()
        .id(MEMBER_ID)
        .workspaceId(WORKSPACE_ID)
        .userId(OWNER_ID)
        .roleId(ROLE_ID)
        .addedAt(CREATED_AT)
        .updatedAt(UPDATED_AT)
        .build();
  }

  public static WorkspaceMember workspaceMemberWithRole(UUID roleId) {
    return WorkspaceMember.builder()
        .id(MEMBER_ID)
        .workspaceId(WORKSPACE_ID)
        .userId(OTHER_USER_ID)
        .roleId(roleId)
        .addedAt(CREATED_AT)
        .updatedAt(UPDATED_AT)
        .addedBy(OWNER_ID)
        .updatedBy(OWNER_ID)
        .build();
  }

  public static RolePermission rolePermission(Permission permission) {
    return RolePermission.builder()
        .id(UUID.fromString("88888888-8888-8888-8888-888888888888"))
        .roleId(ROLE_ID)
        .permission(permission)
        .addedAt(CREATED_AT)
        .build();
  }

  public static <T> DomainPage<T> page(List<T> content) {
    return new DomainPage<>(content, content.size(), 1, 0, content.size());
  }
}
