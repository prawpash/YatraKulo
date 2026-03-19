package com.ekapasha.auth_service.workspace.infrastructure.persistence.repository;

import com.ekapasha.auth_service.shared.domain.pagination.DomainPage;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPageRequest;
import com.ekapasha.auth_service.workspace.domain.entity.WorkspaceMember;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceMemberReadRepository;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.mapper.WorkspaceMemberMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WorkspaceMemberReadRepositoryImpl implements WorkspaceMemberReadRepository {
  private final JPAWorkspaceMemberRepository workspaceMemberRepository;

  public WorkspaceMemberReadRepositoryImpl(JPAWorkspaceMemberRepository workspaceMemberRepository) {
    this.workspaceMemberRepository = workspaceMemberRepository;
  }

  @Override
  public Optional<WorkspaceMember> findById(UUID id) {
    return this.workspaceMemberRepository.findById(id).map(WorkspaceMemberMapper::toDomain);
  }

  @Override
  public Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId) {
    return this.workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
        .map(WorkspaceMemberMapper::toDomain);
  }

  @Override
  public DomainPage<WorkspaceMember> findByWorkspaceId(UUID workspaceId, DomainPageRequest pageRequest) {
    Pageable pageable = PageRequest.of(pageRequest.page(), pageRequest.size());
    Page<WorkspaceMemberEntity> page = this.workspaceMemberRepository.findByWorkspaceId(workspaceId, pageable);

    List<WorkspaceMember> content = page.getContent().stream()
        .map(WorkspaceMemberMapper::toDomain)
        .toList();

    return new DomainPage<>(
        content,
        page.getTotalElements(),
        page.getTotalPages(),
        page.getNumber(),
        page.getSize()
    );
  }

  @Override
  public DomainPage<WorkspaceMember> findByUserId(UUID userId, DomainPageRequest pageRequest) {
    Pageable pageable = PageRequest.of(pageRequest.page(), pageRequest.size());
    Page<WorkspaceMemberEntity> page = this.workspaceMemberRepository.findByUserId(userId, pageable);

    List<WorkspaceMember> content = page.getContent().stream()
        .map(WorkspaceMemberMapper::toDomain)
        .toList();

    return new DomainPage<>(
        content,
        page.getTotalElements(),
        page.getTotalPages(),
        page.getNumber(),
        page.getSize()
    );
  }

  @Override
  public boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId) {
    return this.workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
  }

  @Override
  public long countByWorkspaceId(UUID workspaceId) {
    return this.workspaceMemberRepository.countByWorkspaceId(workspaceId);
  }
}
