package com.ekapasha.auth_service;

import com.ekapasha.shared.exception.DomainRuleViolationException;
import com.ekapasha.shared.exception.DuplicateDataException;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.exception.UnauthorizedAccessException;
import com.ekapasha.shared.exception.ValidationException;
import com.ekapasha.shared.response.ErrorDetail;
import com.ekapasha.shared.response.ErrorResponse;
import com.ekapasha.auth_service.shared.presentation.dto.ApiErrorResponseDto;
import com.ekapasha.shared.logging.AppLogger;
import com.ekapasha.shared.logging.LogEvent;
import com.ekapasha.auth_service.logging.AuthLogEvent;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private final HttpServletRequest request;
  private static final AppLogger logger = new AppLogger(GlobalExceptionHandler.class);

  public GlobalExceptionHandler(HttpServletRequest request) {
    this.request = request;
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiErrorResponseDto> handleNotFoundException(NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiErrorResponseDto.from(new ErrorResponse(404, ex.getMessage(), request.getRequestURI())));
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(DuplicateDataException.class)
  public ResponseEntity<ApiErrorResponseDto> handleDuplicateDataException(DuplicateDataException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiErrorResponseDto.from(new ErrorResponse(409, ex.getMessage(), request.getRequestURI())));
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiErrorResponseDto> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex) {
    logger.warn(
        LogEvent.builder("HTTP message not readable: " + ex.getMessage())
            .eventName(AuthLogEvent.VALIDATION_ERROR)
            .metadata("url.path", request.getRequestURI())
            .error(ex)
            .build());

    // Check if the error is due to a format issue, e.g., an invalid UUID or boolean structure
    if (ex.getCause() instanceof InvalidFormatException ife) {
      logger.debug(ife.getTargetType().toString());
      Class<?> targetType = ife.getTargetType();
      String fieldName = ife.getPath().isEmpty() ? "field" : 
          ife.getPath().stream()
              .map(JacksonException.Reference::getPropertyName)
              .collect(java.util.stream.Collectors.joining("."));

      if (targetType.equals(UUID.class)) {
        var detail = new ErrorDetail(fieldName, "Invalid UUID format");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(ApiErrorResponseDto.from(new ErrorResponse(400, "Validation failed", request.getRequestURI(), List.of(detail))));
      } else if (targetType.equals(boolean.class) || targetType.equals(Boolean.class)) {
        var detail = new ErrorDetail(fieldName, "Invalid boolean format. Must be 'true' or 'false'");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(ApiErrorResponseDto.from(new ErrorResponse(400, "Validation failed", request.getRequestURI(), List.of(detail))));
      }
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiErrorResponseDto.from(new ErrorResponse(400, "Malformed JSON request body", request.getRequestURI())));
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ApiErrorResponseDto> handleValidationException(ValidationException ex) {
    logger.warn(
        LogEvent.builder("Validation failed: " + ex.getMessage())
            .eventName(AuthLogEvent.VALIDATION_ERROR)
            .metadata("url.path", request.getRequestURI())
            .error(ex)
            .build());

    var detail = new ErrorDetail(ex.getProperty(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiErrorResponseDto.from(new ErrorResponse(400, "Validation failed", request.getRequestURI(), List.of(detail))));
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(UnauthorizedAccessException.class)
  public ResponseEntity<ApiErrorResponseDto> handleUnauthorizedException(UnauthorizedAccessException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiErrorResponseDto.from(new ErrorResponse(401, ex.getMessage(), request.getRequestURI())));
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(DomainRuleViolationException.class)
  public ResponseEntity<ApiErrorResponseDto> handleDomainRuleViolationException(
      DomainRuleViolationException ex) {
    logger.warn(
        LogEvent.builder("Domain rule violation: " + ex.getMessage())
            .eventName(AuthLogEvent.VALIDATION_ERROR)
            .metadata("url.path", request.getRequestURI())
            .error(ex)
            .build());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiErrorResponseDto.from(new ErrorResponse(400, ex.getMessage(), request.getRequestURI())));
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponseDto> handleValidationPipeException(
      MethodArgumentNotValidException ex) {
    logger.warn(
        LogEvent.builder("Method argument validation failed: " + ex.getMessage())
            .eventName(AuthLogEvent.VALIDATION_ERROR)
            .metadata("url.path", request.getRequestURI())
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
        .body(ApiErrorResponseDto.from(new ErrorResponse(400, "Validation failed", request.getRequestURI(), details)));
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiErrorResponseDto> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
    logger.warn(
        LogEvent.builder("Method argument type mismatch: " + ex.getMessage())
            .eventName(AuthLogEvent.VALIDATION_ERROR)
            .metadata("url.path", request.getRequestURI())
            .error(ex)
            .build());

    String message = "Invalid type for parameter '" + ex.getName() + "'";
    if (ex.getRequiredType() != null && ex.getRequiredType().equals(UUID.class)) {
      message = "Invalid UUID format for parameter '" + ex.getName() + "'";
    }

    var detail = new ErrorDetail(ex.getName(), message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiErrorResponseDto.from(new ErrorResponse(400, "Validation failed", request.getRequestURI(), List.of(detail))));
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponseDto> handleGenericException(Exception ex) {
    logger.error(
        LogEvent.builder("Unhandled exception occurred: " + ex.getMessage())
            .eventName(AuthLogEvent.UNHANDLED_EXCEPTION)
            .metadata("url.path", request.getRequestURI())
            .error(ex)
            .build());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiErrorResponseDto.from(new ErrorResponse(500, "Internal server error", request.getRequestURI())));
  }
}

