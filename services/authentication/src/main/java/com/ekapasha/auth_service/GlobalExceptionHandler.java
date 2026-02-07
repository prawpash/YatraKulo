package com.ekapasha.auth_service;

import com.ekapasha.auth_service.shared.domain.exception.DuplicateDataException;
import com.ekapasha.auth_service.shared.domain.exception.NotFoundException;
import com.ekapasha.auth_service.shared.domain.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(DuplicateDataException.class)
  public ResponseEntity<String> duplicateDataException(DuplicateDataException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<String> badRequestException(Exception ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
