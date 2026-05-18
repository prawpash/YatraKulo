import { CurrentUser } from '@app/account/interfaces/http/decorators/CurrentUser.decorator';
import { ExecutionContext } from '@nestjs/common';
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
    { factory: (data: unknown, ctx: ExecutionContext) => unknown }
  >;
  return args[Object.keys(args)[0]].factory;
}

describe('CurrentUserDecorator', () => {
  let factory: (data: unknown, ctx: ExecutionContext) => unknown;

  beforeEach(() => {
    factory = getParamDecoratorFactory(CurrentUser);
  });

  it('should return user from request', () => {
    const mockUser = { sub: 'user-1', email: 'test@test.com' };
    const mockRequest = {
      user: mockUser,
    } as Partial<Request>;
    const mockExecutionContext = {
      switchToHttp: jest.fn().mockReturnValue({
        getRequest: jest.fn().mockReturnValue(mockRequest),
      }),
    } as unknown as ExecutionContext;

    const result = factory(null, mockExecutionContext);
    expect(result).toBe(mockUser);
  });
});
