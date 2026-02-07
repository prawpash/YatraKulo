package com.ekapasha.auth_service.role.domain.repository;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPage;
import com.ekapasha.auth_service.shared.domain.pagination.DomainPageRequest;

import java.util.Optional;
import java.util.UUID;

public interface RoleReadRepository {
  public Optional<Role> findById(UUID id);

  public DomainPage<Role> getAll(DomainPageRequest pageRequest, Boolean includeDeleted);

  public DomainPage<Role> getAll(String searchTerm, DomainPageRequest pageRequest, Boolean includeDeleted);

  public long count();

  public long count(String searchTerm);
}
