package com.ekapasha.auth_service.workspace.infrastructure.persistence.repository;

import com.ekapasha.auth_service.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JPAWorkspaceMemberRepository extends JpaRepository<WorkspaceMemberEntity, UUID> {
  Optional<WorkspaceMemberEntity> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

  Page<WorkspaceMemberEntity> findByWorkspaceId(UUID workspaceId, Pageable pageable);

  Page<WorkspaceMemberEntity> findByUserId(UUID userId, Pageable pageable);

  @Query(
      value =
          "SELECT wm FROM WorkspaceMemberEntity wm JOIN UserEntity u ON wm.userId = u.id "
              + "WHERE wm.workspaceId = :workspaceId AND "
              + "(LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))",
      countQuery =
          "SELECT COUNT(wm) FROM WorkspaceMemberEntity wm JOIN UserEntity u ON wm.userId = u.id "
              + "WHERE wm.workspaceId = :workspaceId AND "
              + "(LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
  Page<WorkspaceMemberEntity> findByWorkspaceIdAndUserSearch(
      @Param("workspaceId") UUID workspaceId, @Param("search") String search, Pageable pageable);

  boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

  long countByWorkspaceId(UUID workspaceId);

  void deleteByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);
}
