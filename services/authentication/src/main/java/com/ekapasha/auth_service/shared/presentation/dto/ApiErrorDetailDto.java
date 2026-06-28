package com.ekapasha.auth_service.shared.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApiErrorDetailDto(
    @Schema(description = "The field that failed validation", example = "email")
    String field,
    @Schema(description = "The validation error message", example = "email must be a valid email address")
    String message
) {}
