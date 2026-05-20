package com.ekapasha.auth_service.user.infrastructure.security;

import com.ekapasha.auth_service.user.domain.service.PasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordServiceAdapterTest {

  @Mock private PasswordService passwordService;
  @InjectMocks private PasswordServiceAdapter adapter;

  @Test
  void shouldDelegateEncodeAndMatches() {
    when(passwordService.hashPassword("password123")).thenReturn("hashed");
    when(passwordService.verifyPassword("password123", "hashed")).thenReturn(true);

    assertThat(adapter.encode("password123")).isEqualTo("hashed");
    assertThat(adapter.matches("password123", "hashed")).isTrue();

    verify(passwordService).hashPassword("password123");
    verify(passwordService).verifyPassword("password123", "hashed");
  }

  @Test
  void shouldRejectNullRawPassword() {
    assertThatThrownBy(() -> adapter.encode(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("rawPassword cannot be null");

    assertThatThrownBy(() -> adapter.matches(null, "hashed"))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("rawPassword cannot be null");
  }
}
