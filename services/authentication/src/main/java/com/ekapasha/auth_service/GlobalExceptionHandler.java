package com.ekapasha.auth_service;

import com.ekapasha.shared.exception.DomainRuleViolationException;
import com.ekapasha.shared.exception.DuplicateDataException;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.exception.UnauthorizedAccessException;
import com.ekapasha.shared.exception.ValidationException;
import com.ekapasha.shared.response.ErrorDetail;
import com.ekapasha.shared.response.ErrorResponse;
import com.ekapasha.shared.logging.AppLogger;
import com.ekapasha.shared.logging.LogEvent;
import com.ekapasha.auth_service.logging.AuthLogEvent;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private final HttpServletRequest request;
  private final AppLogger logger = new AppLogger(GlobalExceptionHandler.class);

  public GlobalExceptionHandler(HttpServletRequest request) {
    this.request = request;
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(404, ex.getMessage(), request.getRequestURI()));
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(DuplicateDataException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateDataException(DuplicateDataException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse(409, ex.getMessage(), request.getRequestURI()));
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
    this.logger.warn(
        LogEvent.builder("Validation failed: " + ex.getMessage())
            .eventName(AuthLogEvent.VALIDATION_ERROR)
            .metadata("uri", request.getRequestURI())
            .error(ex)
            .build());

    var detail = new ErrorDetail(ex.getProperty(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            new ErrorResponse(400, "Validation failed", request.getRequestURI(), List.of(detail)));
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(UnauthorizedAccessException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedAccessException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse(401, ex.getMessage(), request.getRequestURI()));
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(DomainRuleViolationException.class)
  public ResponseEntity<ErrorResponse> handleDomainRuleViolationException(
      DomainRuleViolationException ex) {
    this.logger.warn(
        LogEvent.builder("Domain rule violation: " + ex.getMessage())
            .eventName(AuthLogEvent.VALIDATION_ERROR)
            .metadata("uri", request.getRequestURI())
            .error(ex)
            .build());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(400, ex.getMessage(), request.getRequestURI()));
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationPipeException(
      MethodArgumentNotValidException ex) {
    this.logger.warn(
        LogEvent.builder("Method argument validation failed: " + ex.getMessage())
            .eventName(AuthLogEvent.VALIDATION_ERROR)
            .metadata("uri", request.getRequestURI())
            .error(ex)
            .build());

    var details =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                fe ->
                    new ErrorDetail(
                        fe.getField(),
                        fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value"))
            .toList();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(400, "Validation failed", request.getRequestURI(), details));
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    this.logger.error(
        LogEvent.builder("Unhandled exception occurred: " + ex.getMessage())
            .eventName(AuthLogEvent.UNHANDLED_EXCEPTION)
            .metadata("uri", request.getRequestURI())
            .error(ex)
            .build());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(500, "Internal server error", request.getRequestURI()));
  }
}
