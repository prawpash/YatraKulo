package com.ekapasha.auth_service.role.infrastructure.persistence.repository;

import com.ekapasha.auth_service.role.infrastructure.persistence.entity.RoleEntity;
import com.ekapasha.auth_service.role.infrastructure.persistence.entity.RolePermissionEntity;
import com.ekapasha.auth_service.support.PostgresTestSupport;
import com.ekapasha.auth_service.user.infrastructure.persistence.entity.UserEntity;
import com.ekapasha.auth_service.user.infrastructure.persistence.repository.JPAUserRepository;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.repository.JPAWorkspaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class RoleRepositoryIntegrationTest extends PostgresTestSupport {
  @Autowired private JPAPermissionRepository permissionRepository;
  @Autowired private JPARoleRepository roleRepository;
  @Autowired private JPARolePermissionRepository rolePermissionRepository;
  @Autowired private JPAUserRepository userRepository;
  @Autowired private JPAWorkspaceRepository workspaceRepository;

  @Test
  void shouldLoadSeedPermissionsAndOwnerRolePermissions() {
    assertThat(permissionRepository.findByCode("workspace.update")).isPresent();

    RoleEntity ownerRole =
        roleRepository
            .findByWorkspaceIdIsNull(PageRequest.of(0, 10))
            .getContent()
            .stream()
            .filter(role -> "Owner".equals(role.getName()))
            .findFirst()
            .orElseThrow();

    assertThat(rolePermissionRepository.findByRoleId(ownerRole.getId())).hasSize(7);
    assertThat(rolePermissionRepository.existsByRoleIdAndPermissionCode(ownerRole.getId(), "account.read"))
        .isTrue();
  }

  @Test
  void shouldSearchWorkspaceAndGlobalRoles() {
    UUID ownerId = UUID.fromString("99999999-9999-9999-9999-999999999999");
    UUID workspaceId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    persistUser(ownerId);
    persistWorkspace(workspaceId, ownerId);

    roleRepository.saveAndFlush(
        new RoleEntity(
            UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
            workspaceId,
            "Admin",
            "Workspace admin role",
            Instant.parse("2026-04-01T00:00:00Z"),
            Instant.parse("2026-04-01T00:00:00Z"),
            null,
            ownerId,
            ownerId,
            null));
    roleRepository.saveAndFlush(
        new RoleEntity(
            UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"),
            null,
            "Support",
            "Global admin role",
            Instant.parse("2026-04-01T00:00:00Z"),
            Instant.parse("2026-04-01T00:00:00Z"),
            null,
            ownerId,
            ownerId,
            null));

    assertThat(roleRepository.countByWorkspaceId(workspaceId)).isEqualTo(1L);
    assertThat(roleRepository.countByWorkspaceIdIsNull()).isEqualTo(2L);
    var searchPage =
        roleRepository.searchWorkspaceRolesWithGlobal(
            workspaceId, true, "admin", false, PageRequest.of(0, 10));
    assertThat(searchPage.getContent())
        .extracting("name")
        .containsExactlyInAnyOrder("Admin", "Support");
  }

  @Test
  void shouldRejectRolePermissionForMissingPermissionCode() {
    UUID ownerRoleId =
        roleRepository
            .findByWorkspaceIdIsNull(PageRequest.of(0, 10))
            .getContent()
            .stream()
            .filter(role -> "Owner".equals(role.getName()))
            .findFirst()
            .orElseThrow()
            .getId();

    assertThatThrownBy(
            () ->
                rolePermissionRepository.saveAndFlush(
                    new RolePermissionEntity(
                        UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"),
                        ownerRoleId,
                        "missing.permission",
                        Instant.parse("2026-04-01T00:00:00Z"))))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  private void persistUser(UUID ownerId) {
    userRepository.saveAndFlush(
        new UserEntity(
            ownerId,
            "Owner",
            "owner",
            "owner@example.com",
            "$2a$10$012345678901234567890uGx9X9s1T7vY7kD2e1l7w6FJ9T7k0u1K",
            null,
            Instant.parse("2026-04-01T00:00:00Z"),
            Instant.parse("2026-04-01T00:00:00Z")));
  }

  private void persistWorkspace(UUID workspaceId, UUID ownerId) {
    workspaceRepository.saveAndFlush(
        new WorkspaceEntity(
            workspaceId,
            "Workspace",
            "Workspace description",
            ownerId,
            false,
            Instant.parse("2026-04-01T00:00:00Z"),
            Instant.parse("2026-04-01T00:00:00Z")));
  }
}
