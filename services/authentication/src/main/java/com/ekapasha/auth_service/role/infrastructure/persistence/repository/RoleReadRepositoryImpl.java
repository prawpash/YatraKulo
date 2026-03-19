package com.ekapasha.auth_service.role.infrastructure.persistence.repository;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.repository.RoleReadRepository;
import com.ekapasha.auth_service.role.infrastructure.persistence.entity.RoleEntity;
import com.ekapasha.auth_service.role.infrastructure.persistence.mapper.RoleMapper;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPage;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RoleReadRepositoryImpl implements RoleReadRepository {
  private final JPARoleRepository roleRepository;

  public RoleReadRepositoryImpl(
      JPARoleRepository jpaRoleRepository
  ) {
    this.roleRepository = jpaRoleRepository;
  }

  @Override
  public Optional<Role> findById(UUID id) {
    return this.roleRepository.findById(id).map(RoleMapper::toDomain);
  }

  @Override
  public long count() {
    return this.roleRepository.count();
  }

  @Override
  public long count(String searchTerm) {
    if (searchTerm == null || searchTerm.trim().isBlank()) {
      return 0;
    }

    return this.roleRepository.countByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        searchTerm,
        searchTerm
    );
  }

  // Workspace-scoped methods
  @Override
  public Optional<Role> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
    return this.roleRepository.findByIdAndWorkspaceId(id, workspaceId)
        .map(RoleMapper::toDomain);
  }

  @Override
  public long countByWorkspaceId(UUID workspaceId) {
    return this.roleRepository.countByWorkspaceId(workspaceId);
  }

  @Override
  public long countByWorkspaceId(UUID workspaceId, String searchTerm) {
    if (searchTerm == null || searchTerm.trim().isBlank()) {
      return 0;
    }

    return this.roleRepository.countByWorkspaceIdAndSearchTerm(
        workspaceId,
        searchTerm
    );
  }

  @Override
  public long countGlobalRoles() {
    return this.roleRepository.countByWorkspaceIdIsNull();
  }

  @Override
  public long countGlobalRoles(String searchTerm) {
    if (searchTerm == null || searchTerm.trim().isBlank()) {
      return 0;
    }

    return this.roleRepository.countGlobalRolesBySearchTerm(searchTerm);
  }

  @Override
  public DomainPage<Role> getRoles(UUID workspaceId, boolean includeGlobal, String searchTerm, DomainPageRequest pageRequest, boolean includeDeleted) {
    Pageable pageable = PageRequest.of(pageRequest.page(), pageRequest.size());
    String q = (searchTerm == null || searchTerm.trim().isEmpty()) ? "" : searchTerm.trim();

    Page<RoleEntity> roleEntityPage;
    if (workspaceId == null) {
        roleEntityPage = this.roleRepository.searchGlobalRolesBySearchTerm(q, includeDeleted, pageable);
    } else {
        roleEntityPage = this.roleRepository.searchWorkspaceRolesWithGlobal(workspaceId, includeGlobal, q, includeDeleted, pageable);
    }
    
    List<Role> roles = roleEntityPage.stream().map(RoleMapper::toDomain).toList();

    return new DomainPage<>(
        roles,
        roleEntityPage.getTotalElements(),
        roleEntityPage.getTotalPages(),
        roleEntityPage.getNumber(),
        roleEntityPage.getSize()
    );
  }
}
