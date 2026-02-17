package com.ekapasha.auth_service.user.domain.repository;

import com.ekapasha.auth_service.user.domain.entity.User;

public interface UserWriteRepository {
  public User save(User user);
}
