package com.ekapasha.auth_service.user.infrastructure.config;

import com.ekapasha.auth_service.user.application.command.auth.RegisterUserCommandHandler;
import com.ekapasha.auth_service.user.domain.repository.UserReadRepository;
import com.ekapasha.auth_service.user.domain.repository.UserWriteRepository;
import com.ekapasha.auth_service.user.domain.service.PasswordService;
import com.ekapasha.auth_service.user.infrastructure.persistence.repository.JPAUserRepository;
import com.ekapasha.auth_service.user.infrastructure.persistence.repository.UserReadRepositoryImpl;
import com.ekapasha.auth_service.user.infrastructure.persistence.repository.UserWriteRepositoryImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {
  // Repository Beans
  @Bean
  public UserWriteRepository userWriteRepository(JPAUserRepository jpaUserRepository) {
    return new UserWriteRepositoryImpl(jpaUserRepository);
  }

  @Bean
  public UserReadRepository userReadRepository(JPAUserRepository jpaUserRepository) {
    return new UserReadRepositoryImpl(jpaUserRepository);
  }

  @Bean
  public RegisterUserCommandHandler registerUserCommandHandler(
      UserWriteRepository userWriteRepository,
      PasswordService passwordService,
      UserReadRepository userReadRepository
  ) {
    return new RegisterUserCommandHandler(
        userWriteRepository,
        passwordService,
        userReadRepository);
  }
}
