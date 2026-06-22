package com.ekapasha.auth_service.role.infrastructure.persistence.repository;

import com.ekapasha.auth_service.role.infrastructure.persistence.entity.RoleEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JPARoleRepository extends JpaRepository<RoleEntity, UUID> {
    // Existing methods
    Page<
        RoleEntity
    > findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String nameSearchTerm,
        String descriptionSearchTerm,
        Pageable pageable
    );

    long countByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String nameSearchTerm,
        String descriptionSearchTerms
    );

    // Workspace-scoped methods
    Optional<RoleEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    Page<RoleEntity> findByWorkspaceId(UUID workspaceId, Pageable pageable);

    @Query(
        "SELECT r FROM RoleEntity r WHERE (r.workspaceId = :workspaceId OR (:includeGlobal = true AND r.workspaceId IS NULL)) " +
            "AND (:includeDeleted = true OR r.deletedAt IS NULL) " +
            "AND (LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))"
    )
    Page<RoleEntity> searchWorkspaceRolesWithGlobal(
        @Param("workspaceId") UUID workspaceId,
        @Param("includeGlobal") boolean includeGlobal,
        @Param("searchTerm") String searchTerm,
        @Param("includeDeleted") boolean includeDeleted,
        Pageable pageable
    );

    long countByWorkspaceId(UUID workspaceId);

    @Query(
        "SELECT COUNT(r) FROM RoleEntity r WHERE r.workspaceId = :workspaceId " +
            "AND (LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))"
    )
    long countByWorkspaceIdAndSearchTerm(
        @Param("workspaceId") UUID workspaceId,
        @Param("searchTerm") String searchTerm
    );

    // Global roles methods (workspaceId = null)
    Page<RoleEntity> findByWorkspaceIdIsNull(Pageable pageable);

    @Query(
        "SELECT r FROM RoleEntity r WHERE r.workspaceId IS NULL " +
            "AND (:includeDeleted = true OR r.deletedAt IS NULL) " +
            "AND (LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))"
    )
    Page<RoleEntity> searchGlobalRolesBySearchTerm(
        @Param("searchTerm") String searchTerm,
        @Param("includeDeleted") boolean includeDeleted,
        Pageable pageable
    );

    long countByWorkspaceIdIsNull();

    @Query(
        "SELECT COUNT(r) FROM RoleEntity r WHERE r.workspaceId IS NULL " +
            "AND (LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))"
    )
    long countGlobalRolesBySearchTerm(@Param("searchTerm") String searchTerm);
}
