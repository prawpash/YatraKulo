package com.ekapasha.auth_service.shared.domain.exception;

public class DuplicateDataException extends RuntimeException {
  public DuplicateDataException(String message) {
    super(message);
  }
}
