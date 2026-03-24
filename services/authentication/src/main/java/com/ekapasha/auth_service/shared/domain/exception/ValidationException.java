package com.ekapasha.auth_service.shared.domain.exception;

public class ValidationException extends RuntimeException {
  private final String property;

  public ValidationException(String property, String message) {
    super(message);
    this.property = property;
  }

  public String getProperty() {
    return property;
  }
}
