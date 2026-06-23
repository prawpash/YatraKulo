package com.ekapasha.auth_service.user.infrastructure.persistence.repository;

import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ekapasha.auth_service.user.UserTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserWriteRepositoryImplTest {

  @Mock private JPAUserRepository jpaUserRepository;

  @Test
  void shouldSaveMappedEntityAndReturnDomainObject() {
    UserWriteRepositoryImpl repository = new UserWriteRepositoryImpl(jpaUserRepository);
    User user = user();
    when(jpaUserRepository.save(org.mockito.ArgumentMatchers.any(UserEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    User result = repository.save(user);

    ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
    verify(jpaUserRepository).save(captor.capture());
    UserEntity saved = captor.getValue();
    assertThat(saved.getId()).isEqualTo(USER_ID);
    assertThat(saved.getUsername()).isEqualTo("jane");
    assertThat(result.getEmail()).isEqualTo("jane@example.com");
  }
}
