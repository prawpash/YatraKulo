package com.ekapasha.shared.cqrs;

public interface QueryHandler<Q, R>{
  R handler(Q query);
}
