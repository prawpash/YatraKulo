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
}

