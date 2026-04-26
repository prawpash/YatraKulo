package com.ekapasha.auth_service.workspace.infrastructure.persistence.repository;

import com.ekapasha.shared.pagination.DomainPage;
import com.ekapasha.shared.pagination.DomainPageRequest;
import com.ekapasha.auth_service.workspace.domain.entity.Workspace;
import com.ekapasha.auth_service.workspace.domain.repository.WorkspaceReadRepository;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.ekapasha.auth_service.workspace.infrastructure.persistence.mapper.WorkspaceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WorkspaceReadRepositoryImpl implements WorkspaceReadRepository {
  private final JPAWorkspaceRepository workspaceRepository;

  public WorkspaceReadRepositoryImpl(JPAWorkspaceRepository workspaceRepository) {
    this.workspaceRepository = workspaceRepository;
  }

  @Override
  public Optional<Workspace> findById(UUID id) {
    return this.workspaceRepository.findById(id).map(WorkspaceMapper::toDomain);
  }

  @Override
  public Optional<Workspace> findByIdAndOwnerId(UUID id, UUID ownerId) {
    return this.workspaceRepository.findByIdAndOwnerId(id, ownerId).map(WorkspaceMapper::toDomain);
  }

  @Override
  public List<Workspace> findByOwnerId(UUID ownerId) {
    return this.workspaceRepository.findByOwnerId(ownerId).stream()
        .map(WorkspaceMapper::toDomain)
        .toList();
  }

  @Override
  public DomainPage<Workspace> findByOwnerId(UUID ownerId, DomainPageRequest pageRequest) {
    Pageable pageable = PageRequest.of(pageRequest.page(), pageRequest.size());
    Page<WorkspaceEntity> page = this.workspaceRepository.findByOwnerId(ownerId, pageable);
    
    List<Workspace> content = page.getContent().stream()
        .map(WorkspaceMapper::toDomain)
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
  public DomainPage<Workspace> findByOwnerIdAndSearch(UUID ownerId, String search, DomainPageRequest pageRequest) {
    if (search == null || search.isBlank()) {
      return this.findByOwnerId(ownerId, pageRequest);
    }
    
    Pageable pageable = PageRequest.of(pageRequest.page(), pageRequest.size());
    Page<WorkspaceEntity> page = this.workspaceRepository.findByOwnerIdAndNameContainingIgnoreCase(ownerId, search, pageable);
    
    List<Workspace> content = page.getContent().stream()
        .map(WorkspaceMapper::toDomain)
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
  public Optional<Workspace> findDefaultByOwnerId(UUID ownerId) {
    return this.workspaceRepository.findByOwnerIdAndIsDefaultTrue(ownerId).map(WorkspaceMapper::toDomain);
  }

  @Override
  public boolean existsByIdAndOwnerId(UUID id, UUID ownerId) {
    return this.workspaceRepository.existsByIdAndOwnerId(id, ownerId);
  }

  @Override
  public long count() {
    return this.workspaceRepository.count();
  }
}
