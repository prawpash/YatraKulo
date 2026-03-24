package com.ekapasha.auth_service.shared.domain.exception;

public class DomainRuleViolationException extends RuntimeException {
  public DomainRuleViolationException(String message) {
    super(message);
  }
}
