package com.ekapasha.auth_service.user.presentation.controller;

import com.ekapasha.auth_service.user.application.command.auth.RegisterUserCommand;
import com.ekapasha.auth_service.user.application.command.auth.RegisterUserCommandHandler;
import com.ekapasha.auth_service.user.presentation.dto.RegisterUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
  private final RegisterUserCommandHandler registerUserCommandHandler;

  @GetMapping("/login")
  public String loginPage() {
    return "login";
  }

  @GetMapping("/register")
  public String registerPage(Model model) {
    model.addAttribute("registerRequest", new RegisterUserRequest(null, null, null, null, null));
    return "register";
  }

  @PostMapping("/register")
  public String register(
      @Valid @ModelAttribute("registerRequest") RegisterUserRequest request,
      BindingResult bindingResult,
      Model model
  ) {
    if (bindingResult.hasErrors()) {
      return "register";
    }

    try {
      RegisterUserCommand command = new RegisterUserCommand(
          request.name(),
          request.username(),
          request.email(),
          request.password(),
          request.profilePictureURL()
      );

      this.registerUserCommandHandler.handler(command);
      return "redirect:/login?registered=true";
    } catch (Exception e) {
      // We can catch specific exceptions like DuplicateDataException here if needed
      model.addAttribute("errorMessage", e.getMessage());
      return "register";
    }
  }
}

