package com.ekapasha.auth_service.user.application.query;

import com.ekapasha.auth_service.shared.application.query.QueryHandler;
import com.ekapasha.auth_service.shared.domain.exception.NotFoundException;
import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.domain.repository.UserReadRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetUserByEmailQueryHandler implements QueryHandler<GetUserByEmailQuery, User> {
  private final UserReadRepository userReadRepository;

  @Override
  public User handler(GetUserByEmailQuery query) {
    return this.userReadRepository
        .findByEmail(query.email())
        .orElseThrow(() -> new NotFoundException("User not found"));
  }
}
