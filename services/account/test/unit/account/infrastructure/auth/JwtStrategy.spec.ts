import { Test, TestingModule } from '@nestjs/testing';
import { JwtStrategy } from '@app/account/infrastructure/auth/JwtStrategy';
import { ConfigService } from '@nestjs/config';
import { HttpService } from '@nestjs/axios';
import { PermissionsCache } from '@app/account/infrastructure/auth/PermissionsCache';
import { of, throwError } from 'rxjs';
import { UnauthorizedException } from '@nestjs/common';
import { Request } from 'express';
import { JwtPayload } from '@app/account/infrastructure/auth/JwtStrategy';

describe('JwtStrategy', () => {
  let strategy: JwtStrategy;
  let configService: jest.Mocked<ConfigService>;
  let httpService: jest.Mocked<HttpService>;
  let permissionsCache: jest.Mocked<PermissionsCache>;
  let cacheSetMock: jest.Mock;

  beforeEach(async () => {
    configService = {
      get: jest.fn().mockImplementation((key: string) => {
        if (key === 'auth.jwks_uri') return 'http://jwks';
        if (key === 'auth.issuer') return 'issuer';
        if (key === 'auth.service_url') return 'http://auth';
        return null;
      }),
    } as unknown as jest.Mocked<ConfigService>;
    httpService = {
      get: jest.fn(),
    } as unknown as jest.Mocked<HttpService>;
    cacheSetMock = jest.fn();
    permissionsCache = {
      get: jest.fn(),
      set: cacheSetMock,
      delete: jest.fn(),
      flush: jest.fn(),
    } as unknown as jest.Mocked<PermissionsCache>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        JwtStrategy,
        { provide: ConfigService, useValue: configService },
        { provide: HttpService, useValue: httpService },
        { provide: PermissionsCache, useValue: permissionsCache },
      ],
    }).compile();

    strategy = module.get<JwtStrategy>(JwtStrategy);
  });

  it('should validate and return payload with permissions', async () => {
    const payload = { sub: 'user-1' };
    const workspaceId = '00000000-0000-0000-0000-000000000000';
    const req = {
      headers: {
        'x-workspace-id': workspaceId,
        authorization: 'Bearer token',
      },
    };

    permissionsCache.get.mockReturnValue(['read']);

    const result = await strategy.validate(
      req as unknown as Request,
      payload as JwtPayload,
    );

    expect(result).toEqual({
      ...payload,
      permissions: ['read'],
    });
  });

  it('should throw UnauthorizedException if workspace ID is missing', async () => {
    const payload = { sub: 'user-1' };
    const req = {
      headers: {},
    };

    await expect(
      strategy.validate(req as unknown as Request, payload as JwtPayload),
    ).rejects.toThrow(UnauthorizedException);
  });

  it('should fetch permissions if not cached', async () => {
    const payload = { sub: 'user-1' };
    const workspaceId = '00000000-0000-0000-0000-000000000000';
    const req = {
      headers: {
        'x-workspace-id': workspaceId,
        authorization: 'Bearer token',
      },
    };

    permissionsCache.get.mockReturnValue(null);
    httpService.get.mockReturnValue(of({ data: ['write'] }));

    const result = await strategy.validate(
      req as unknown as Request,
      payload as JwtPayload,
    );

    expect(result.permissions).toEqual(['write']);
    expect(cacheSetMock).toHaveBeenCalled();
  });

  it('should throw UnauthorizedException if authorization header is missing', async () => {
    const payload = { sub: 'user-1' };
    const req = {
      headers: {
        'x-workspace-id': 'ws-1',
      },
    };

    await expect(
      strategy.validate(req as unknown as Request, payload as JwtPayload),
    ).rejects.toThrow(UnauthorizedException);
  });

  it('should throw UnauthorizedException if auth service fetch fails', async () => {
    const payload = { sub: 'user-1' };
    const req = {
      headers: {
        'x-workspace-id': 'ws-1',
        authorization: 'Bearer token',
      },
    };

    permissionsCache.get.mockReturnValue(null);
    httpService.get.mockReturnValue(throwError(() => new Error('Service down')));

    await expect(
      strategy.validate(req as unknown as Request, payload as JwtPayload),
    ).rejects.toThrow(UnauthorizedException);
  });
});
