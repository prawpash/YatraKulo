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
  public DomainPage<Role> getAll(DomainPageRequest pageRequest, Boolean includeDeleted) {
    Pageable pageable = PageRequest.of(pageRequest.page(), pageRequest.size());

    Page<RoleEntity> roleEntityPage = this.roleRepository.findAll(pageable);

    List<Role> roles = roleEntityPage.stream().map(RoleMapper::toDomain).toList();

    return new DomainPage<>(
        roles,
        roleEntityPage.getTotalElements(),
        roleEntityPage.getTotalPages(),
        roleEntityPage.getNumber(),
        roleEntityPage.getSize()
    );
  }

  @Override
  public DomainPage<Role> getAll(String searchTerm, DomainPageRequest pageRequest, Boolean includeDeleted) {
    Pageable pageable = PageRequest.of(pageRequest.page(), pageRequest.size());

    Page<RoleEntity> roleEntityPage = this
        .roleRepository
        .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            searchTerm,
            searchTerm,
            pageable
        );

    List<Role> roles = roleEntityPage.stream().map(RoleMapper::toDomain).toList();

    return new DomainPage<>(
        roles,
        roleEntityPage.getTotalElements(),
        roleEntityPage.getTotalPages(),
        roleEntityPage.getNumber(),
        roleEntityPage.getSize()
    );
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
  public DomainPage<Role> getAllByWorkspaceId(UUID workspaceId, DomainPageRequest pageRequest, Boolean includeDeleted) {
    Pageable pageable = PageRequest.of(pageRequest.page(), pageRequest.size());

    Page<RoleEntity> roleEntityPage = this.roleRepository.findByWorkspaceId(workspaceId, pageable);

    List<Role> roles = roleEntityPage.stream().map(RoleMapper::toDomain).toList();

    return new DomainPage<>(
        roles,
        roleEntityPage.getTotalElements(),
        roleEntityPage.getTotalPages(),
        roleEntityPage.getNumber(),
        roleEntityPage.getSize()
    );
  }

  @Override
  public DomainPage<Role> getAllByWorkspaceId(UUID workspaceId, String searchTerm, DomainPageRequest pageRequest, Boolean includeDeleted) {
    Pageable pageable = PageRequest.of(pageRequest.page(), pageRequest.size());

    Page<RoleEntity> roleEntityPage = this
        .roleRepository
        .searchByWorkspaceIdAndSearchTerm(
            workspaceId,
            searchTerm,
            pageable
        );

    List<Role> roles = roleEntityPage.stream().map(RoleMapper::toDomain).toList();

    return new DomainPage<>(
        roles,
        roleEntityPage.getTotalElements(),
        roleEntityPage.getTotalPages(),
        roleEntityPage.getNumber(),
        roleEntityPage.getSize()
    );
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

  // Global roles methods (workspaceId = null)
  @Override
  public DomainPage<Role> getGlobalRoles(DomainPageRequest pageRequest, Boolean includeDeleted) {
    Pageable pageable = PageRequest.of(pageRequest.page(), pageRequest.size());

    Page<RoleEntity> roleEntityPage = this.roleRepository.findByWorkspaceIdIsNull(pageable);

    List<Role> roles = roleEntityPage.stream().map(RoleMapper::toDomain).toList();

    return new DomainPage<>(
        roles,
        roleEntityPage.getTotalElements(),
        roleEntityPage.getTotalPages(),
        roleEntityPage.getNumber(),
        roleEntityPage.getSize()
    );
  }

  @Override
  public DomainPage<Role> getGlobalRoles(String searchTerm, DomainPageRequest pageRequest, Boolean includeDeleted) {
    Pageable pageable = PageRequest.of(pageRequest.page(), pageRequest.size());

    Page<RoleEntity> roleEntityPage = this.roleRepository.searchGlobalRolesBySearchTerm(searchTerm, pageable);

    List<Role> roles = roleEntityPage.stream().map(RoleMapper::toDomain).toList();

    return new DomainPage<>(
        roles,
        roleEntityPage.getTotalElements(),
        roleEntityPage.getTotalPages(),
        roleEntityPage.getNumber(),
        roleEntityPage.getSize()
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
}
