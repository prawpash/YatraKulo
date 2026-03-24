package com.ekapasha.auth_service.user.domain.repository;

import com.ekapasha.auth_service.user.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserReadRepository {
  public Optional<User> findById(UUID id);

  public Optional<User> findByUsername(String username);

  public Optional<User> findByEmail(String email);

  public long count();
}
