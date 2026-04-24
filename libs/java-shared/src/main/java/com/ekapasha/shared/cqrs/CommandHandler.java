package com.ekapasha.shared.cqrs;

public interface CommandHandler<C, R> {
  R handler(C command);
}


