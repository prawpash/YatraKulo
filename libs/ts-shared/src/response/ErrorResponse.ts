import { z } from 'zod';

export const ErrorDetailSchema = z.object({
  field: z.string(),
  message: z.string(),
});

export const ErrorResponseSchema = z.object({
  statusCode: z.number(),
  error: z.string(),
  message: z.string(),
  timestamp: z.iso.datetime(),
  path: z.string(),
  traceId: z.string().optional(),
  details: z.array(ErrorDetailSchema).optional(),
});

export type ErrorResponse = z.infer<typeof ErrorResponseSchema>;
export type ErrorDetail = z.infer<typeof ErrorDetailSchema>;

export function createErrorResponse(
  statusCode: number,
  message: string,
  path: string,
  traceId?: string,
  details?: ErrorDetail[],
): ErrorResponse {
  return {
    statusCode,
    error: getErrorLabel(statusCode),
    message,
    timestamp: new Date().toISOString(),
    path,
    traceId,
    details,
  };
}

function getErrorLabel(statusCode: number): string {
  const labels: Record<number, string> = {
    400: 'Bad Request',
    401: 'Unauthorized',
    403: 'Forbidden',
    404: 'Not Found',
    409: 'Conflict',
    422: 'Unprocessable Entity',
    500: 'Internal Server Error',
  };
  return labels[statusCode] || 'Error';
}
