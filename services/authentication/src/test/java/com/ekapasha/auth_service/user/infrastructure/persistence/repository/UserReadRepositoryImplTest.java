package com.ekapasha.auth_service.user.infrastructure.persistence.repository;

import com.ekapasha.auth_service.user.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.ekapasha.auth_service.user.UserTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserReadRepositoryImplTest {

  @Mock private JPAUserRepository jpaUserRepository;

  @Test
  void shouldDelegateAndMapResults() {
    UserReadRepositoryImpl repository = new UserReadRepositoryImpl(jpaUserRepository);
    UserEntity entity = userEntity();
    when(jpaUserRepository.findById(USER_ID)).thenReturn(Optional.of(entity));
    when(jpaUserRepository.findByUsername("jane")).thenReturn(Optional.of(entity));
    when(jpaUserRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(entity));
    when(jpaUserRepository.count()).thenReturn(4L);

    assertThat(repository.findById(USER_ID)).hasValueSatisfying(user -> assertThat(user.getUsername()).isEqualTo("jane"));
    assertThat(repository.findByUsername("jane")).hasValueSatisfying(user -> assertThat(user.getEmail()).isEqualTo("jane@example.com"));
    assertThat(repository.findByEmail("jane@example.com")).hasValueSatisfying(user -> assertThat(user.getName()).isEqualTo("Jane Doe"));
    assertThat(repository.count()).isEqualTo(4L);
  }
}
