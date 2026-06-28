package com.ekapasha.auth_service.shared.presentation.dto;

import com.ekapasha.shared.response.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

public record ApiErrorResponseDto(
    @Schema(description = "HTTP status code", example = "400")
    int statusCode,

    @Schema(description = "Error name/category", example = "Bad Request")
    String error,

    @Schema(description = "Detailed error message", example = "Validation failed")
    String message,

    @Schema(description = "Timestamp when error occurred", example = "2026-06-28T10:00:00Z")
    Instant timestamp,

    @Schema(description = "Request path that caused the error", example = "/api/v1/roles")
    String path,

    @Schema(description = "Trace ID for tracking in logs", example = "trace-123456")
    String traceId,

    @Schema(description = "List of detailed field errors, if validation failed")
    List<ApiErrorDetailDto> details
) {
  public static ApiErrorResponseDto from(ErrorResponse response) {
    List<ApiErrorDetailDto> detailsDto = response.details() == null ? null :
        response.details().stream()
            .map(d -> new ApiErrorDetailDto(d.field(), d.message()))
            .toList();

    return new ApiErrorResponseDto(
        response.statusCode(),
        response.error(),
        response.message(),
        response.timestamp(),
        response.path(),
        response.traceId(),
        detailsDto
    );
  }
}
