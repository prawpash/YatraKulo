package com.ekapasha.shared.cqrs;

public interface VoidCommandHandler<C> {
  void handler(C command);
}