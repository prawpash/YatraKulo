package com.ekapasha.auth_service.workspace.infrastructure.persistence.repository;

import com.ekapasha.auth_service.role.infrastructure.persistence.entity.RoleEntity;
import com.ekapasha.auth_service.role.infrastructure.persistence.repository.JPARoleRepository;
import com.ekapasha.auth_service.support.PostgresTestSupport;
import com.ekapasha.auth_service.user.infrastructure.persistence.entity.UserEntity;
import com.ekapasha.auth_service.user.infrastructure.persistence.repository.JPAUserRepository;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class WorkspaceRepositoryIntegrationTest extends PostgresTestSupport {
  @Autowired private JPAUserRepository userRepository;
  @Autowired private JPAWorkspaceRepository workspaceRepository;
  @Autowired private JPAWorkspaceMemberRepository workspaceMemberRepository;
  @Autowired private JPARoleRepository roleRepository;
  @Autowired private EntityManager entityManager;

  @Test
  void shouldSetDefaultWorkspaceAndQueryByOwnerAndName() {
    UUID ownerId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
    persistUser(ownerId, "owner");

    WorkspaceEntity first =
        new WorkspaceEntity(
            UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee"),
            "Alpha Workspace",
            "First",
            ownerId,
            true,
            Instant.parse("2026-04-01T00:00:00Z"),
            Instant.parse("2026-04-01T00:00:00Z"));
    WorkspaceEntity second =
        new WorkspaceEntity(
            UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff"),
            "Beta Workspace",
            "Second",
            ownerId,
            false,
            Instant.parse("2026-04-01T00:00:00Z"),
            Instant.parse("2026-04-01T00:00:00Z"));

    workspaceRepository.saveAndFlush(first);
    workspaceRepository.saveAndFlush(second);

    assertThat(workspaceRepository.findByOwnerId(ownerId)).hasSize(2);
    var searchPage =
        workspaceRepository.findByOwnerIdAndNameContainingIgnoreCase(
            ownerId, "beta", PageRequest.of(0, 10));
    assertThat(searchPage.getContent())
        .extracting("name")
        .containsExactly("Beta Workspace");

    workspaceRepository.setDefault(second.getId(), ownerId);
    entityManager.flush();
    entityManager.clear();

    assertThat(workspaceRepository.findByOwnerIdAndIsDefaultTrue(ownerId))
        .hasValueSatisfying(workspace -> assertThat(workspace.getId()).isEqualTo(second.getId()));
    assertThat(workspaceRepository.existsByIdAndOwnerId(second.getId(), ownerId)).isTrue();
  }

  @Test
  void shouldRejectSecondDefaultWorkspaceForSameOwner() {
    UUID ownerId = UUID.fromString("11111111-2222-3333-4444-555555555555");
    persistUser(ownerId, "duplicate-owner");

    WorkspaceEntity first =
        new WorkspaceEntity(
            UUID.fromString("22222222-2222-2222-2222-222222222222"),
            "Primary",
            null,
            ownerId,
            true,
            Instant.parse("2026-04-01T00:00:00Z"),
            Instant.parse("2026-04-01T00:00:00Z"));
    WorkspaceEntity second =
        new WorkspaceEntity(
            UUID.fromString("33333333-3333-3333-3333-333333333333"),
            "Secondary",
            null,
            ownerId,
            true,
            Instant.parse("2026-04-01T00:00:00Z"),
            Instant.parse("2026-04-01T00:00:00Z"));

    workspaceRepository.saveAndFlush(first);

    assertThatThrownBy(() -> workspaceRepository.saveAndFlush(second))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void shouldPersistWorkspaceMemberAndSearchByUserNameOrEmail() {
    UUID ownerId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    UUID memberUserId = UUID.fromString("55555555-5555-5555-5555-555555555555");
    UUID workspaceId = UUID.fromString("66666666-6666-6666-6666-666666666666");
    UUID roleId = UUID.fromString("77777777-7777-7777-7777-777777777777");

    persistUser(ownerId, "workspace-owner");
    persistUser(memberUserId, "jane");

    workspaceRepository.saveAndFlush(
        new WorkspaceEntity(
            workspaceId,
            "Support Workspace",
            "Members are searchable",
            ownerId,
            false,
            Instant.parse("2026-04-01T00:00:00Z"),
            Instant.parse("2026-04-01T00:00:00Z")));

    persistRole(roleId, workspaceId);

    workspaceMemberRepository.saveAndFlush(
        new WorkspaceMemberEntity(
            UUID.fromString("88888888-8888-8888-8888-888888888888"),
            workspaceId,
            memberUserId,
            roleId,
            Instant.parse("2026-04-01T00:00:00Z"),
            Instant.parse("2026-04-01T00:00:00Z"),
            ownerId,
            ownerId));

    assertThat(
            workspaceMemberRepository.findByWorkspaceIdAndUserSearch(
                workspaceId, "jane", PageRequest.of(0, 10)))
        .extracting("userId")
        .containsExactly(memberUserId);
    assertThat(workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, memberUserId))
        .isTrue();
    assertThat(workspaceMemberRepository.countByWorkspaceId(workspaceId)).isEqualTo(1L);
  }

  private void persistUser(UUID id, String username) {
    userRepository.saveAndFlush(
        new UserEntity(
            id,
            "User " + username,
            username,
            username + "@example.com",
            "$2a$10$012345678901234567890uGx9X9s1T7vY7kD2e1l7w6FJ9T7k0u1K",
            null,
            Instant.parse("2026-04-01T00:00:00Z"),
            Instant.parse("2026-04-01T00:00:00Z")));
  }

  private void persistRole(UUID roleId, UUID workspaceId) {
    roleRepository.saveAndFlush(
        new RoleEntity(
            roleId,
            workspaceId,
            "Member",
            "Workspace member role",
            Instant.parse("2026-04-01T00:00:00Z"),
            Instant.parse("2026-04-01T00:00:00Z"),
            null,
            null,
            null,
            null));
  }
}
