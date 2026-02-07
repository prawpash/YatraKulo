package com.ekapasha.auth_service.role.domain.repository;

import com.ekapasha.auth_service.role.domain.entity.Role;

public interface RoleWriteRepository {
  public Role save(Role role);
}
