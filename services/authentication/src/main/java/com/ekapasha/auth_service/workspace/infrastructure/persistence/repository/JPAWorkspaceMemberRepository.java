package com.ekapasha.auth_service.workspace.infrastructure.persistence.repository;

import com.ekapasha.auth_service.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JPAWorkspaceMemberRepository extends JpaRepository<WorkspaceMemberEntity, UUID> {
  Optional<WorkspaceMemberEntity> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

  Page<WorkspaceMemberEntity> findByWorkspaceId(UUID workspaceId, Pageable pageable);

  Page<WorkspaceMemberEntity> findByUserId(UUID userId, Pageable pageable);

  boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

  long countByWorkspaceId(UUID workspaceId);

  void deleteByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);
}
