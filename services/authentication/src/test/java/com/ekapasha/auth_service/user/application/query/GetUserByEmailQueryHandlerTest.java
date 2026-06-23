package com.ekapasha.auth_service.user.application.query;

import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.domain.repository.UserReadRepository;
import com.ekapasha.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ekapasha.auth_service.user.UserTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserByEmailQueryHandlerTest {

  @Mock private UserReadRepository userReadRepository;
  @InjectMocks private GetUserByEmailQueryHandler handler;

  @Test
  void shouldReturnUserWhenFound() {
    User user = user();
    when(userReadRepository.findByEmail("jane@example.com")).thenReturn(java.util.Optional.of(user));

    User result = handler.handler(new GetUserByEmailQuery("jane@example.com"));

    assertThat(result).isSameAs(user);
  }

  @Test
  void shouldThrowWhenMissing() {
    when(userReadRepository.findByEmail("missing@example.com")).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> handler.handler(new GetUserByEmailQuery("missing@example.com")))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("User not found");
  }
}
