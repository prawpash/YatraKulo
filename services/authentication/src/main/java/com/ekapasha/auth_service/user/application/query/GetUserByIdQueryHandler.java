package com.ekapasha.auth_service.user.application.query;

import com.ekapasha.shared.cqrs.QueryHandler;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.domain.repository.UserReadRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetUserByIdQueryHandler implements QueryHandler<GetUserByIdQuery, User> {
  private final UserReadRepository userReadRepository;

  @Override
  public User handler(GetUserByIdQuery query) {
    return this.userReadRepository
        .findById(query.id())
        .orElseThrow(() -> new NotFoundException("User not found"));
  }
}
