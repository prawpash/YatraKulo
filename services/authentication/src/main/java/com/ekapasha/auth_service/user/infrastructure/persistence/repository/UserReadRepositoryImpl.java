package com.ekapasha.auth_service.user.infrastructure.persistence.repository;

import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.domain.repository.UserReadRepository;
import com.ekapasha.auth_service.user.infrastructure.persistence.mapper.UserMapper;

import java.util.Optional;
import java.util.UUID;

public class UserReadRepositoryImpl implements UserReadRepository {
  private final JPAUserRepository userRepository;

  public UserReadRepositoryImpl(JPAUserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Optional<User> findById(UUID id) {
    return this.userRepository.findById(id).map(UserMapper::toDomain);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return this.userRepository.findByUsername(username).map(UserMapper::toDomain);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return this.userRepository.findByEmail(email).map(UserMapper::toDomain);
  }

  @Override
  public long count() {
    return this.userRepository.count();
  }
}
