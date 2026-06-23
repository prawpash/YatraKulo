package com.ekapasha.auth_service.user.infrastructure.persistence.mapper;

import com.ekapasha.auth_service.user.domain.entity.User;
import org.junit.jupiter.api.Test;

import static com.ekapasha.auth_service.user.UserTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

  @Test
  void shouldMapEntityToDomainAndBack() {
    User domain = UserMapper.toDomain(userEntity());
    assertThat(domain.getId()).isEqualTo(USER_ID);
    assertThat(domain.getProfilePictureURL()).contains("https://example.com/avatar.png");

    var entity = UserMapper.toEntity(domain);
    assertThat(entity.getId()).isEqualTo(USER_ID);
    assertThat(entity.getUsername()).isEqualTo("jane");
    assertThat(entity.getProfilePictureURL()).isEqualTo("https://example.com/avatar.png");
  }
}
