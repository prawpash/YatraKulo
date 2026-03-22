package com.ekapasha.auth_service.role.infrastructure.persistence.repository;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.role.domain.repository.PermissionReadRepository;
import com.ekapasha.auth_service.role.infrastructure.persistence.entity.PermissionEntity;
import com.ekapasha.auth_service.role.infrastructure.persistence.mapper.PermissionMapper;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPage;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public class PermissionReadRepositoryImpl implements PermissionReadRepository {

  private final JPAPermissionRepository permissionRepository;

  public PermissionReadRepositoryImpl(JPAPermissionRepository permissionRepository) {
    this.permissionRepository = permissionRepository;
  }

  @Override
  public DomainPage<Permission> getAll(String search, DomainPageRequest pageRequest) {
    Pageable pageable = PageRequest.of(pageRequest.page(), pageRequest.size());

    Page<PermissionEntity> permissionEntityPage;
    if (search == null || search.isBlank()) {
      permissionEntityPage = this.permissionRepository.findAll(pageable);
    } else {
      permissionEntityPage =
          this.permissionRepository.findByCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
              search, search, pageable);
    }

    List<Permission> permissions =
        permissionEntityPage.stream().map(PermissionMapper::toDomain).toList();

    return new DomainPage<>(
        permissions,
        permissionEntityPage.getTotalElements(),
        permissionEntityPage.getTotalPages(),
        permissionEntityPage.getNumber(),
        permissionEntityPage.getSize());
  }

  @Override
  public Optional<Permission> findByCode(String code) {
    return this.permissionRepository.findByCode(code).map(PermissionMapper::toDomain);
  }
}
