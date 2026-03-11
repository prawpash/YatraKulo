package com.ekapasha.auth_service.user.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record RegisterUserRequest(
  @NotBlank(message = "Name must not be empty")
  @Size(min = 3, message = "Name must be at least 3 characters long")
  String name,

  @NotBlank(message = "Username must not be empty")
  @Size(min = 3, message = "Username must be at least 3 characters long")
  String username,

  @NotBlank(message = "Email must not be empty")
  @Email(message = "Email must be valid")
  String email,

  @NotBlank(message = "Password must not be empty")
  @Size(min = 8, message = "Password must be at least 8 characters long")
  String password,

  @URL(message = "Profile picture URL must be valid")
  String profilePictureURL
) {}

