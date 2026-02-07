package com.ekapasha.auth_service.role.infrastructure.persistence.mapper;

import com.ekapasha.auth_service.role.domain.entity.Role;
import com.ekapasha.auth_service.role.infrastructure.persistence.entity.RoleEntity;

public class RoleMapper {
  public static Role toDomain(RoleEntity roleEntity){
    return Role.builder()
        .id(roleEntity.getId())
        .name(roleEntity.getName())
        .description(roleEntity.getDescription())
        .createdAt(roleEntity.getCreatedAt())
        .updatedAt(roleEntity.getUpdatedAt())
        .deletedAt(roleEntity.getDeletedAt())
        .createdBy(roleEntity.getCreatedBy())
        .updatedBy(roleEntity.getUpdatedBy())
        .deletedBy(roleEntity.getDeletedBy())
        .build();
  }

  public static RoleEntity toEntity(Role role){
    return new RoleEntity(
        role.getId(),
        role.getName(),
        role.getDescription(),
        role.getCreatedAt(),
        role.getUpdatedAt(),
        role.getDeletedAt().orElse(null),
        role.getCreatedBy().orElse(null),
        role.getUpdatedBy().orElse(null),
        role.getDeletedBy().orElse(null)
    );
  }
}
