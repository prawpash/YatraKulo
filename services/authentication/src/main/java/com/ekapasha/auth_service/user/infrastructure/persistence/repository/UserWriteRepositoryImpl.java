package com.ekapasha.auth_service.user.infrastructure.persistence.repository;

import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.domain.repository.UserWriteRepository;
import com.ekapasha.auth_service.user.infrastructure.persistence.entity.UserEntity;
import com.ekapasha.auth_service.user.infrastructure.persistence.mapper.UserMapper;

public class UserWriteRepositoryImpl implements UserWriteRepository {
  private final JPAUserRepository userRepository;

  public UserWriteRepositoryImpl(JPAUserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User save(User user) {
    UserEntity userEntity = UserMapper.toEntity(user);
    this.userRepository.save(userEntity);
    return UserMapper.toDomain(userEntity);
  }
}
