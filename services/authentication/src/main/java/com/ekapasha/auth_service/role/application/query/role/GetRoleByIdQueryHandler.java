package com.ekapasha.auth_service.role.application.query.role;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.domain.repository.RoleReadRepository;
import com.ekapasha.shared.cqrs.QueryHandler;
import com.ekapasha.shared.exception.NotFoundException;

public class GetRoleByIdQueryHandler implements QueryHandler<GetRoleByIdQuery, Role> {
  private final RoleReadRepository roleReadRepository;

  public GetRoleByIdQueryHandler(
      RoleReadRepository roleReadRepository
  ){
    this.roleReadRepository = roleReadRepository;
  }

  @Override
  public Role handler(GetRoleByIdQuery query) {
    return this.roleReadRepository.findById(query.id()).orElseThrow(() -> new NotFoundException("Role not found"));
  }
}
