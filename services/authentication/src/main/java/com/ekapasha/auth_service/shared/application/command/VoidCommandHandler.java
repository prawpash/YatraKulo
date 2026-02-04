package com.ekapasha.auth_service.shared.application.command;

public interface VoidCommandHandler<C> {
  void handler(C command);
}