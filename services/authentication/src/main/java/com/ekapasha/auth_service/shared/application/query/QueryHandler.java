package com.ekapasha.auth_service.shared.application.query;

public interface QueryHandler<Q, R>{
  R handler(Q query);
}
