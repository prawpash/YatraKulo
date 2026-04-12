package com.ekapasha.auth_service.workspace.infrastructure.persistence.repository;

import com.ekapasha.auth_service.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JPAWorkspaceRepository extends JpaRepository<WorkspaceEntity, UUID> {
  Optional<WorkspaceEntity> findByIdAndOwnerId(UUID id, UUID ownerId);

  List<WorkspaceEntity> findByOwnerId(UUID ownerId);

  Page<WorkspaceEntity> findByOwnerId(UUID ownerId, Pageable pageable);

  Page<WorkspaceEntity> findByOwnerIdAndNameContainingIgnoreCase(
      UUID ownerId, String name, Pageable pageable);

  Optional<WorkspaceEntity> findByOwnerIdAndIsDefaultTrue(UUID ownerId);

  boolean existsByIdAndOwnerId(UUID id, UUID ownerId);

  @Modifying
  @Query(
      value =
          "UPDATE workspace "
              + "SET is_default = CASE "
              + "    WHEN id = :workspaceId THEN true "
              + "    ELSE false "
              + "END "
              + "WHERE owner_id = :ownerId "
              + "AND EXISTS ( "
              + "    SELECT 1 FROM workspace target "
              + "    WHERE target.id = :workspaceId "
              + "      AND target.owner_id = :ownerId "
              + ") "
              + "AND ( "
              + "    is_default = true "
              + "    OR id = :workspaceId "
              + ")",
      nativeQuery = true)
  void setDefault(@Param("workspaceId") UUID workspaceId, @Param("ownerId") UUID ownerId);
}
