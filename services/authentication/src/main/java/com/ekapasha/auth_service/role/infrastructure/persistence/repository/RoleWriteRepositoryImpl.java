package com.ekapasha.auth_service.role.infrastructure.persistence.repository;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.repository.RoleWriteRepository;
import com.ekapasha.auth_service.role.infrastructure.persistence.entity.RoleEntity;
import com.ekapasha.auth_service.role.infrastructure.persistence.mapper.RoleMapper;

public class RoleWriteRepositoryImpl implements RoleWriteRepository {

  private final JPARoleRepository roleRepository;

  public RoleWriteRepositoryImpl(
      JPARoleRepository jpaRoleRepository
  ) {
    this.roleRepository = jpaRoleRepository;
  }

  @Override
  public Role save(Role role) {
    RoleEntity roleEntity = RoleMapper.toEntity(role);
    this.roleRepository.save(roleEntity);
    return RoleMapper.toDomain(roleEntity);
  }
}
