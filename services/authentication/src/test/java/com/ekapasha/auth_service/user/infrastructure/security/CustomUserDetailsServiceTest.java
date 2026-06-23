package com.ekapasha.auth_service.user.infrastructure.security;

import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.domain.repository.UserReadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.ekapasha.auth_service.user.UserTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

  @Mock private UserReadRepository userReadRepository;
  @InjectMocks private CustomUserDetailsService service;

  @Test
  void shouldLoadUserByUsername() {
    User user = user();
    when(userReadRepository.findByUsername("jane")).thenReturn(java.util.Optional.of(user));

    UserDetails result = service.loadUserByUsername("jane");

    assertThat(result.getUsername()).isEqualTo("jane");
    assertThat(result.getPassword()).isEqualTo(user.getHashedPassword());
    assertThat(result.getAuthorities()).isEmpty();
  }

  @Test
  void shouldThrowWhenMissing() {
    when(userReadRepository.findByUsername("missing")).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> service.loadUserByUsername("missing"))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage("User not found");
  }
}
