package com.ekapasha.shared.response;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
    int statusCode,
    String error,
    String message,
    Instant timestamp,
    String path,
    String traceId,
    List<ErrorDetail> details) {
  public ErrorResponse(int statusCode, String message, String path) {
    this(statusCode, getErrorLabel(statusCode), message, Instant.now(), path, null, null);
  }

  public ErrorResponse(int statusCode, String message, String path, String traceId) {
    this(statusCode, getErrorLabel(statusCode), message, Instant.now(), path, traceId, null);
  }

  public ErrorResponse(int statusCode, String message, String path, List<ErrorDetail> details) {
    this(statusCode, getErrorLabel(statusCode), message, Instant.now(), path, null, details);
  }

  public ErrorResponse(int statusCode, String message, String path, String traceId, List<ErrorDetail> details) {
    this(statusCode, getErrorLabel(statusCode), message, Instant.now(), path, traceId, details);
  }

  private static String getErrorLabel(int statusCode) {
    return switch (statusCode) {
      case 400 -> "Bad Request";
      case 401 -> "Unauthorized";
      case 403 -> "Forbidden";
      case 404 -> "Not Found";
      case 409 -> "Conflict";
      case 422 -> "Unprocessable Entity";
      case 500 -> "Internal Server Error";
      default -> "Error";
    };
  }
}
