package com.ekapasha.auth_service.user.application.command.auth;

public record RegisterUserCommand(
    String name,
    String username,
    String email,
    String password,
    String profilePictureURL
) {}
