package com.ekapasha.auth_service.user.infrastructure.config;

import com.ekapasha.auth_service.user.application.command.auth.RegisterUserCommandHandler;
import com.ekapasha.auth_service.user.domain.repository.UserReadRepository;
import com.ekapasha.auth_service.user.domain.repository.UserWriteRepository;
import com.ekapasha.auth_service.user.domain.service.PasswordService;
import com.ekapasha.auth_service.user.infrastructure.persistence.repository.JPAUserRepository;
import com.ekapasha.auth_service.user.infrastructure.persistence.repository.UserReadRepositoryImpl;
import com.ekapasha.auth_service.user.infrastructure.persistence.repository.UserWriteRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class UserConfigTest {

  @Test
  void shouldCreateUserBeans() {
    UserConfig config = new UserConfig();
    JPAUserRepository jpaUserRepository = Mockito.mock(JPAUserRepository.class);
    PasswordService passwordService = Mockito.mock(PasswordService.class);

    UserWriteRepository writeRepository = config.userWriteRepository(jpaUserRepository);
    UserReadRepository readRepository = config.userReadRepository(jpaUserRepository);
    RegisterUserCommandHandler handler =
        config.registerUserCommandHandler(writeRepository, passwordService, readRepository);

    assertThat(writeRepository).isInstanceOf(UserWriteRepositoryImpl.class);
    assertThat(readRepository).isInstanceOf(UserReadRepositoryImpl.class);
    assertThat(handler).isNotNull();
  }
}
