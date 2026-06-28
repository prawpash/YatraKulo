package com.ekapasha.auth_service;

import com.ekapasha.shared.exception.DomainRuleViolationException;
import com.ekapasha.shared.exception.DuplicateDataException;
import com.ekapasha.shared.exception.NotFoundException;
import com.ekapasha.shared.exception.UnauthorizedAccessException;
import com.ekapasha.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  @Mock private HttpServletRequest request;

  @Test
  void shouldMapDomainExceptionsToErrorResponses() throws Exception {
    when(request.getRequestURI()).thenReturn("/api/test");

    GlobalExceptionHandler handler = new GlobalExceptionHandler(request);

    assertThat(handler.handleNotFoundException(new NotFoundException("missing")).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(handler.handleNotFoundException(new NotFoundException("missing")).getBody().toString())
        .contains("missing", "/api/test");

    assertThat(handler.handleDuplicateDataException(new DuplicateDataException("dup")).getStatusCode())
        .isEqualTo(HttpStatus.CONFLICT);
    assertThat(handler.handleDuplicateDataException(new DuplicateDataException("dup")).getBody().toString())
        .contains("dup", "/api/test");

    assertThat(handler.handleUnauthorizedException(new UnauthorizedAccessException("nope")).getStatusCode())
        .isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(handler.handleUnauthorizedException(new UnauthorizedAccessException("nope")).getBody().toString())
        .contains("nope", "/api/test");

    assertThat(handler.handleDomainRuleViolationException(new DomainRuleViolationException("bad")).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(handler.handleDomainRuleViolationException(new DomainRuleViolationException("bad")).getBody().toString())
        .contains("bad", "/api/test");
  }

  @Test
  void shouldMapValidationExceptionsToStructuredErrorResponse() {
    when(request.getRequestURI()).thenReturn("/api/test");
    GlobalExceptionHandler handler = new GlobalExceptionHandler(request);

    ValidationException exception = new ValidationException("name", "must not be blank");

    var response = handler.handleValidationException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().toString()).contains("Validation failed", "name", "must not be blank");
  }

  @Test
  void shouldMapMethodArgumentNotValidExceptionToStructuredErrorResponse() throws Exception {
    when(request.getRequestURI()).thenReturn("/api/test");
    GlobalExceptionHandler handler = new GlobalExceptionHandler(request);

    BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "input");
    bindingResult.addError(new FieldError("input", "name", "must not be blank"));
    bindingResult.addError(new FieldError("input", "email", null));

    Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyEndpoint", String.class);
    MethodParameter parameter = new MethodParameter(method, 0);
    MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

    var response = handler.handleValidationPipeException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().toString()).contains("Validation failed", "name", "must not be blank", "email", "Invalid value");
  }

  @Test
  void shouldHandleMethodArgumentTypeMismatchForUUID() throws Exception {
    when(request.getRequestURI()).thenReturn("/api/test");
    GlobalExceptionHandler handler = new GlobalExceptionHandler(request);

    Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyEndpoint", String.class);
    MethodParameter parameter = new MethodParameter(method, 0);

    MethodArgumentTypeMismatchException exception =
        new MethodArgumentTypeMismatchException(
            "invalid-uuid", java.util.UUID.class, "id", parameter, new IllegalArgumentException("Invalid UUID string"));

    var response = handler.handleMethodArgumentTypeMismatchException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().toString()).contains("Validation failed", "id", "Invalid UUID format for parameter 'id'");
  }

  @Test
  void shouldHandleMethodArgumentTypeMismatchForOtherTypes() throws Exception {
    when(request.getRequestURI()).thenReturn("/api/test");
    GlobalExceptionHandler handler = new GlobalExceptionHandler(request);

    Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyEndpoint", String.class);
    MethodParameter parameter = new MethodParameter(method, 0);

    MethodArgumentTypeMismatchException exception =
        new MethodArgumentTypeMismatchException(
            "invalid-int", Integer.class, "age", parameter, new NumberFormatException("For input string: \"invalid-int\""));

    var response = handler.handleMethodArgumentTypeMismatchException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().toString()).contains("Validation failed", "age", "Invalid type for parameter 'age'");
  }

  @Test
  void shouldMapGenericExceptionsToInternalServerError() {
    when(request.getRequestURI()).thenReturn("/api/test");
    GlobalExceptionHandler handler = new GlobalExceptionHandler(request);

    var response = handler.handleGenericException(new RuntimeException("boom"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody().toString()).contains("Internal server error", "/api/test");
  }

  @SuppressWarnings("unused")
  private void dummyEndpoint(String input) {}
}
