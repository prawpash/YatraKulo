package com.ekapasha.auth_service.user.application.command.user;

import java.util.UUID;

public record UpdateUserProfileCommand(
    UUID id,
    String name,
    String username,
    String email,
    String profilePictureURL,
    UUID invokedBy
) {}
