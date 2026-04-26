package com.ekapasha.auth_service.role.domain.repository;

import com.ekapasha.auth_service.role.domain.enums.Permission;
import com.ekapasha.shared.pagination.DomainPage;
import com.ekapasha.shared.pagination.DomainPageRequest;

import java.util.Optional;

public interface PermissionReadRepository {
  public DomainPage<Permission> getAll(String search, DomainPageRequest pageRequest);

  public Optional<Permission> findByCode(String code);
}
