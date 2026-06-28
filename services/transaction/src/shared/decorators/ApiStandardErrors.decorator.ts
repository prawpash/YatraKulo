import { applyDecorators } from '@nestjs/common';
import { ApiResponse } from '@nestjs/swagger';
import { ErrorResponseDto } from '@app/shared/dto/ErrorResponse.dto';

export function ApiStandardErrors() {
  return applyDecorators(
    ApiResponse({
      status: 400,
      description: 'Bad Request (e.g., Validation Error)',
      type: ErrorResponseDto,
    }),
    ApiResponse({
      status: 401,
      description: 'Unauthorized - Missing or Invalid JWT',
      type: ErrorResponseDto,
    }),
    ApiResponse({
      status: 403,
      description: 'Forbidden - Insufficient permissions',
      type: ErrorResponseDto,
    }),
    ApiResponse({
      status: 404,
      description: 'Not Found',
      type: ErrorResponseDto,
    }),
    ApiResponse({
      status: 409,
      description: 'Conflict - Duplicate Data',
      type: ErrorResponseDto,
    }),
    ApiResponse({
      status: 500,
      description: 'Internal Server Error',
      type: ErrorResponseDto,
    }),
  );
}
