import { XWorkspaceId } from '@app/account/interfaces/http/decorators/XWorkspaceId.decorator';
import { ExecutionContext, UnauthorizedException } from '@nestjs/common';
import { Request } from 'express';
import { ROUTE_ARGS_METADATA } from '@nestjs/common/constants';

function getParamDecoratorFactory(
  decorator: (...args: unknown[]) => ParameterDecorator,
) {
  class Test {
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    test(@decorator() _value: unknown) {}
  }
  const args = Reflect.getMetadata(ROUTE_ARGS_METADATA, Test, 'test') as Record<
    string,
    { factory: (data: unknown, ctx: ExecutionContext) => string }
  >;
  return args[Object.keys(args)[0]].factory;
}

describe('XWorkspaceIdDecorator', () => {
  let factory: (data: unknown, ctx: ExecutionContext) => string;

  beforeEach(() => {
    factory = getParamDecoratorFactory(XWorkspaceId);
  });

  it('should return workspaceId from header', () => {
    const workspaceId = '00000000-0000-0000-0000-000000000000';
    const mockRequest = {
      headers: {
        'x-workspace-id': workspaceId,
      },
    } as Partial<Request>;
    const mockExecutionContext = {
      switchToHttp: jest.fn().mockReturnValue({
        getRequest: jest.fn().mockReturnValue(mockRequest),
      }),
    } as unknown as ExecutionContext;

    const result = factory(null, mockExecutionContext);
    expect(result).toBe(workspaceId);
  });

  it('should throw UnauthorizedException if header is missing or not a UUID', () => {
    const mockRequest = {
      headers: {},
    } as Partial<Request>;
    const mockExecutionContext = {
      switchToHttp: jest.fn().mockReturnValue({
        getRequest: jest.fn().mockReturnValue(mockRequest),
      }),
    } as unknown as ExecutionContext;

    expect(() => factory(null, mockExecutionContext)).toThrow(
      UnauthorizedException,
    );
  });
});
