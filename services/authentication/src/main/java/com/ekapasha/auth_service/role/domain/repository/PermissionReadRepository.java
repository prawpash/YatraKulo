package com.ekapasha.auth_service.role.domain.repository;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPage;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPageRequest;

import java.util.Optional;

public interface PermissionReadRepository {
  public DomainPage<Permission> getAll(DomainPageRequest pageRequest);

  public DomainPage<Permission> getAll(String searchTerm, DomainPageRequest pageRequest);

  public Optional<Permission> findByCode(String code);
}
