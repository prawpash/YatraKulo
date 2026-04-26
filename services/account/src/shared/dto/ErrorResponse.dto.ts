import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class ErrorDetailDto {
  @ApiProperty({
    example: 'email',
    description: 'The field that failed validation',
  })
  field!: string;

  @ApiProperty({
    example: 'email must be an email',
    description: 'The validation error message',
  })
  message!: string;
}

export class ErrorResponseDto {
  @ApiProperty({ example: 400 })
  statusCode!: number;

  @ApiProperty({ example: 'Bad Request' })
  error!: string;

  @ApiProperty({ example: 'Validation failed' })
  message!: string;

  @ApiProperty({ example: '2026-04-26T15:00:00.000Z', format: 'date-time' })
  timestamp!: string;

  @ApiProperty({ example: '/accounts' })
  path!: string;

  @ApiPropertyOptional({ example: 'trace-123456' })
  traceId?: string;

  @ApiPropertyOptional({ type: [ErrorDetailDto] })
  details?: ErrorDetailDto[];
}
