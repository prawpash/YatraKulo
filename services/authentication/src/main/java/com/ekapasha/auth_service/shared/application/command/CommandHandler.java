package com.ekapasha.auth_service.shared.application.command;

public interface CommandHandler<C, R> {
  R handler(C command);
}


