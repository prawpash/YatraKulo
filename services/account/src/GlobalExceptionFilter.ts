import {
  ExceptionFilter,
  Catch,
  ArgumentsHost,
  HttpException,
  HttpStatus,
  BadRequestException,
} from '@nestjs/common';
import { AppLogger } from '@yk/shared';
import { Request, Response } from 'express';
import {
  NotFoundException,
  DuplicateDataException,
  ValidationException,
  UnauthorizedAccessException,
  DomainRuleViolationException,
  ErrorResponse,
  ErrorDetail,
  createErrorResponse,
} from '@yk/shared';

@Catch()
export class GlobalExceptionFilter implements ExceptionFilter {
  private readonly logger = new AppLogger(GlobalExceptionFilter.name);

  catch(exception: unknown, host: ArgumentsHost) {
    const ctx = host.switchToHttp();
    const response = ctx.getResponse<Response>();
    const request = ctx.getRequest<Request>();

    const errorResponse = this.mapException(exception, request);

    this.logger.error(`${errorResponse.statusCode} ${errorResponse.message}`);
    // this.logger.error(
    //   `${errorResponse.statusCode} ${errorResponse.message}`,
    //   exception instanceof Error ? exception.stack : undefined,
    // );

    response.status(errorResponse.statusCode).json(errorResponse);
  }

  private mapException(exception: unknown, request: Request): ErrorResponse {
    const path = request.url;
    const traceId = request.headers['x-trace-id'] as string | undefined;

    // // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    // this.logger.debug(Object.getPrototypeOf(exception).constructor);

    if (exception instanceof NotFoundException) {
      return createErrorResponse(404, exception.message, path, traceId);
    }

    if (exception instanceof DuplicateDataException) {
      return createErrorResponse(409, exception.message, path, traceId);
    }

    if (exception instanceof ValidationException) {
      const details: ErrorDetail[] | undefined = exception.field
        ? [{ field: exception.field, message: exception.message }]
        : undefined;
      return createErrorResponse(
        400,
        'Validation failed',
        path,
        traceId,
        details,
      );
    }

    if (exception instanceof UnauthorizedAccessException) {
      return createErrorResponse(401, exception.message, path, traceId);
    }

    if (exception instanceof DomainRuleViolationException) {
      return createErrorResponse(400, exception.message, path, traceId);
    }

    if (exception instanceof BadRequestException) {
      const res = exception.getResponse();
      let message = 'Validation failed';
      let details: ErrorDetail[] | undefined = undefined;

      if (typeof res === 'object' && res !== null) {
        const resObj = res as Record<string, unknown>;
        if (Array.isArray(resObj.message)) {
          details = resObj.message.map((m: unknown) => {
            if (typeof m === 'object' && m !== null) {
              const obj = m as Record<string, unknown>;
              return {
                field: typeof obj.field === 'string' ? obj.field : 'unknown',
                message: Array.isArray(obj.message)
                  ? obj.message.join(', ')
                  : typeof obj.message === 'string'
                    ? obj.message
                    : String(obj.message),
              };
            }
            return { field: 'unknown', message: String(m) };
          });
        } else if (typeof resObj.message === 'string') {
          message = resObj.message;
        }
      }

      return createErrorResponse(400, message, path, traceId, details);
    }

    if (exception instanceof HttpException) {
      const status = exception.getStatus();
      const res = exception.getResponse();

      let message = exception.message;
      let details: ErrorDetail[] | undefined = undefined;

      if (typeof res === 'object' && res !== null) {
        const resObj = res as Record<string, unknown>;
        if (typeof resObj.message === 'string') {
          message = resObj.message;
        } else if (Array.isArray(resObj.message)) {
          message = 'Validation failed';
          details = resObj.message.map((m: unknown) => {
            if (typeof m === 'string') {
              return { field: 'unknown', message: m };
            }
            if (typeof m === 'object' && m !== null) {
              const obj = m as Record<string, unknown>;
              return {
                field: typeof obj.field === 'string' ? obj.field : 'unknown',
                message: Array.isArray(obj.message)
                  ? obj.message.join(', ')
                  : typeof obj.message === 'string'
                    ? obj.message
                    : String(obj.message),
              };
            }
            return { field: 'unknown', message: String(m) };
          });
        }
      } else if (typeof res === 'string') {
        message = res;
      }

      return createErrorResponse(status, message, path, traceId, details);
    }

    // Catch-all: unexpected error
    this.logger.error(
      'Unhandled exception',
      exception instanceof Error ? exception.stack : undefined,
    );
    return createErrorResponse(
      HttpStatus.INTERNAL_SERVER_ERROR,
      'Internal server error',
      path,
      traceId,
    );
  }
}
