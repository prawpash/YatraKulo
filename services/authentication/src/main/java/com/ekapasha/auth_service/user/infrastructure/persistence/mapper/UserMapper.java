package com.ekapasha.auth_service.user.infrastructure.persistence.mapper;

import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.infrastructure.persistence.entity.UserEntity;

public class UserMapper {
  public static User toDomain(UserEntity userEntity) {
    return User.builder()
        .id(userEntity.getId())
        .name(userEntity.getName())
        .username(userEntity.getUsername())
        .email(userEntity.getEmail())
        .hashedPassword(userEntity.getHashedPassword())
        .profilePictureURL(userEntity.getProfilePictureURL())
        .createdAt(userEntity.getCreatedAt())
        .updatedAt(userEntity.getUpdatedAt())
        .build();
  }

  public static UserEntity toEntity(User user) {
    return new UserEntity(
        user.getId(),
        user.getName(),
        user.getUsername(),
        user.getEmail(),
        user.getHashedPassword(),
        user.getProfilePictureURL().orElse(null),
        user.getCreatedAt(),
        user.getUpdatedAt());
  }
}
