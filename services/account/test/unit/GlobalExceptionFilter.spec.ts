import { GlobalExceptionFilter } from '@app/GlobalExceptionFilter';
import {
  ArgumentsHost,
  BadRequestException,
  UnauthorizedException,
  ForbiddenException,
} from '@nestjs/common';
import {
  NotFoundException,
  ValidationException,
  DomainRuleViolationException,
} from '@yk/shared';
import { Request, Response } from 'express';

describe('GlobalExceptionFilter', () => {
  let filter: GlobalExceptionFilter;
  let mockArgumentsHost: jest.Mocked<ArgumentsHost>;
  let mockResponse: jest.Mocked<Response>;
  let mockRequest: jest.Mocked<Request>;
  let statusMock: jest.Mock;
  let jsonMock: jest.Mock;

  beforeEach(() => {
    filter = new GlobalExceptionFilter();
    statusMock = jest.fn().mockReturnThis();
    jsonMock = jest.fn().mockReturnThis();
    mockResponse = {
      status: statusMock,
      json: jsonMock,
    } as unknown as jest.Mocked<Response>;
    mockRequest = {
      url: '/test',
      headers: {},
    } as unknown as jest.Mocked<Request>;
    mockArgumentsHost = {
      switchToHttp: () => ({
        getResponse: () => mockResponse,
        getRequest: () => mockRequest,
      }),
    } as unknown as jest.Mocked<ArgumentsHost>;
  });

  it('should map NotFoundException to 404', () => {
    const exception = new NotFoundException('Not found');
    filter.catch(exception, mockArgumentsHost as ArgumentsHost);

    expect(statusMock).toHaveBeenCalledWith(404);
    expect(jsonMock).toHaveBeenCalledWith(
      expect.objectContaining({
        statusCode: 404,
        message: 'Not found',
      }),
    );
  });

  it('should map ValidationException to 400', () => {
    const exception = new ValidationException('Invalid', 'field');
    filter.catch(exception, mockArgumentsHost as ArgumentsHost);

    expect(statusMock).toHaveBeenCalledWith(400);
    expect(jsonMock).toHaveBeenCalledWith(
      expect.objectContaining({
        statusCode: 400,
        message: 'Validation failed',
        details: [{ field: 'field', message: 'Invalid' }],
      }),
    );
  });

  it('should map DomainRuleViolationException to 400', () => {
    const exception = new DomainRuleViolationException('Rule violated');
    filter.catch(exception, mockArgumentsHost as ArgumentsHost);

    expect(statusMock).toHaveBeenCalledWith(400);
    expect(jsonMock).toHaveBeenCalledWith(
      expect.objectContaining({
        statusCode: 400,
        message: 'Rule violated',
      }),
    );
  });

  it('should map BadRequestException from NestJS to 400', () => {
    const exception = new BadRequestException('Bad request');
    filter.catch(exception, mockArgumentsHost as ArgumentsHost);

    expect(statusMock).toHaveBeenCalledWith(400);
    expect(jsonMock).toHaveBeenCalledWith(
      expect.objectContaining({
        statusCode: 400,
        message: 'Bad request',
      }),
    );
  });

  it('should map unknown exception to 500', () => {
    const exception = new Error('Unknown');
    filter.catch(exception, mockArgumentsHost as ArgumentsHost);

    expect(statusMock).toHaveBeenCalledWith(500);
    expect(jsonMock).toHaveBeenCalledWith(
      expect.objectContaining({
        statusCode: 500,
        message: 'Internal server error',
      }),
    );
  });

  it('should map UnauthorizedException to 401', () => {
    const exception = new UnauthorizedException('Auth failed');
    filter.catch(exception, mockArgumentsHost as ArgumentsHost);

    expect(statusMock).toHaveBeenCalledWith(401);
    expect(jsonMock).toHaveBeenCalledWith(
      expect.objectContaining({
        statusCode: 401,
        message: 'Auth failed',
      }),
    );
  });

  it('should map ForbiddenException to 403', () => {
    const exception = new ForbiddenException('Access denied');
    filter.catch(exception, mockArgumentsHost as ArgumentsHost);

    expect(statusMock).toHaveBeenCalledWith(403);
    expect(jsonMock).toHaveBeenCalledWith(
      expect.objectContaining({
        statusCode: 403,
        message: 'Access denied',
      }),
    );
  });

  it('should map NestJS BadRequestException with multiple errors to 400 with details', () => {
    const errors = [
      { field: 'name', message: 'Too long' },
      { field: 'type', message: 'Required' },
    ];
    const exception = new BadRequestException({
      message: errors,
    });
    filter.catch(exception, mockArgumentsHost as ArgumentsHost);

    expect(statusMock).toHaveBeenCalledWith(400);
    expect(jsonMock).toHaveBeenCalledWith(
      expect.objectContaining({
        statusCode: 400,
        details: expect.arrayContaining(errors),
      }),
    );
  });
});
