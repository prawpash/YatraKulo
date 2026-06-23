package com.ekapasha.auth_service.user.presentation.controller;

import com.ekapasha.auth_service.user.application.command.auth.RegisterUserCommand;
import com.ekapasha.auth_service.user.application.command.auth.RegisterUserCommandHandler;
import com.ekapasha.auth_service.user.presentation.dto.RegisterUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static com.ekapasha.auth_service.user.UserTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock private RegisterUserCommandHandler registerUserCommandHandler;
  @Mock private Model model;
  @Mock private BindingResult bindingResult;
  @InjectMocks private AuthController controller;

  @Test
  void shouldReturnLoginView() {
    assertThat(controller.loginPage()).isEqualTo("login");
  }

  @Test
  void shouldSeedRegisterPageModel() {
    String view = controller.registerPage(model);

    verify(model).addAttribute(
        "registerRequest", new RegisterUserRequest(null, null, null, null, null));
    assertThat(view).isEqualTo("register");
  }

  @Test
  void shouldReturnRegisterWhenBindingHasErrors() {
    when(bindingResult.hasErrors()).thenReturn(true);

    String view =
        controller.register(
            new RegisterUserRequest("Jane Doe", "jane", "jane@example.com", "password123", null),
            bindingResult,
            model);

    verify(registerUserCommandHandler, never()).handler(any());
    assertThat(view).isEqualTo("register");
  }

  @Test
  void shouldRedirectAfterSuccessfulRegistration() {
    when(bindingResult.hasErrors()).thenReturn(false);
    when(registerUserCommandHandler.handler(any())).thenReturn(user());

    String view =
        controller.register(
            new RegisterUserRequest("Jane Doe", "jane", "jane@example.com", "password123", null),
            bindingResult,
            model);

    ArgumentCaptor<RegisterUserCommand> captor = ArgumentCaptor.forClass(RegisterUserCommand.class);
    verify(registerUserCommandHandler).handler(captor.capture());
    assertThat(captor.getValue().name()).isEqualTo("Jane Doe");
    assertThat(view).isEqualTo("redirect:/login?registered=true");
  }

  @Test
  void shouldReturnRegisterWithErrorMessageWhenHandlerFails() {
    when(bindingResult.hasErrors()).thenReturn(false);
    when(registerUserCommandHandler.handler(any())).thenThrow(new IllegalStateException("boom"));

    String view =
        controller.register(
            new RegisterUserRequest("Jane Doe", "jane", "jane@example.com", "password123", null),
            bindingResult,
            model);

    verify(model).addAttribute("errorMessage", "boom");
    assertThat(view).isEqualTo("register");
  }
}
