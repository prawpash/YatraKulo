import { Test, TestingModule } from '@nestjs/testing';
import { PermissionsGuard } from '@app/transaction/infrastructure/auth/PermissionsGuard';
import { Reflector } from '@nestjs/core';
import { ForbiddenException, ExecutionContext } from '@nestjs/common';

describe('PermissionsGuard', () => {
  let guard: PermissionsGuard;
  let reflector: jest.Mocked<Reflector>;

  beforeEach(async () => {
    reflector = {
      getAllAndOverride: jest.fn(),
    } as unknown as jest.Mocked<Reflector>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        PermissionsGuard,
        { provide: Reflector, useValue: reflector },
      ],
    }).compile();

    guard = module.get<PermissionsGuard>(PermissionsGuard);
  });

  it('should allow access if no permissions are required', () => {
    reflector.getAllAndOverride.mockReturnValue([]);
    const context = {
      getHandler: () => () => {},
      getClass: () => class {},
    };
    expect(guard.canActivate(context as unknown as ExecutionContext)).toBe(
      true,
    );
  });

  it('should allow access if user has all required permissions', () => {
    const required = ['read', 'write'];
    reflector.getAllAndOverride.mockReturnValue(required);
    const mockRequest = {
      user: {
        permissions: ['read', 'write', 'extra'],
      },
    };
    const context = {
      getHandler: () => () => {},
      getClass: () => class {},
      switchToHttp: () => ({
        getRequest: () => mockRequest,
      }),
    };
    expect(guard.canActivate(context as unknown as ExecutionContext)).toBe(
      true,
    );
  });

  it('should throw ForbiddenException if user misses some permissions', () => {
    const required = ['read', 'write'];
    reflector.getAllAndOverride.mockReturnValue(required);
    const mockRequest = {
      user: {
        permissions: ['read'],
      },
    };
    const context = {
      getHandler: () => () => {},
      getClass: () => class {},
      switchToHttp: () => ({
        getRequest: () => mockRequest,
      }),
    };
    expect(() =>
      guard.canActivate(context as unknown as ExecutionContext),
    ).toThrow(ForbiddenException);
  });

  it('should throw ForbiddenException if user has no permissions key', () => {
    const required = ['read'];
    reflector.getAllAndOverride.mockReturnValue(required);
    const mockRequest = {
      user: {},
    };
    const context = {
      getHandler: () => () => {},
      getClass: () => class {},
      switchToHttp: () => ({
        getRequest: () => mockRequest,
      }),
    };
    expect(() =>
      guard.canActivate(context as unknown as ExecutionContext),
    ).toThrow(ForbiddenException);
  });
});
