import { Test, TestingModule } from '@nestjs/testing';
import { JwtStrategy } from '@app/transaction/infrastructure/auth/JwtStrategy';
import { ConfigService } from '@nestjs/config';
import { HttpService } from '@nestjs/axios';
import { PermissionsCache } from '@app/transaction/infrastructure/auth/PermissionsCache';
import { of, throwError } from 'rxjs';
import { UnauthorizedException } from '@nestjs/common';
import { Request } from 'express';
import { JwtPayload } from '@app/transaction/infrastructure/auth/JwtStrategy';

describe('JwtStrategy', () => {
  let strategy: JwtStrategy;
  let configService: jest.Mocked<ConfigService>;
  let httpService: jest.Mocked<HttpService>;
  let permissionsCache: jest.Mocked<PermissionsCache>;
  let cacheGetMock: jest.Mock;
  let cacheSetMock: jest.Mock;
  let httpGetMock: jest.Mock;

  beforeEach(async () => {
    configService = {
      get: jest.fn().mockImplementation((key: string) => {
        if (key === 'auth.jwks_uri') return 'http://jwks';
        if (key === 'auth.issuer') return 'issuer';
        if (key === 'auth.service_url') return 'http://auth';
        return null;
      }),
    } as unknown as jest.Mocked<ConfigService>;

    httpGetMock = jest.fn();
    httpService = {
      get: httpGetMock,
    } as unknown as jest.Mocked<HttpService>;

    cacheGetMock = jest.fn();
    cacheSetMock = jest.fn();
    permissionsCache = {
      get: cacheGetMock,
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

  it('should validate and return payload with permissions from cache', async () => {
    const payload = { sub: 'user-1' } as unknown as JwtPayload;
    const workspaceId = '00000000-0000-0000-0000-000000000000';
    const req = {
      headers: {
        'x-workspace-id': workspaceId,
        authorization: 'Bearer token',
      },
    } as unknown as Request;

    cacheGetMock.mockReturnValue(['read']);

    const result = await strategy.validate(req, payload);

    expect(result).toEqual({
      ...payload,
      permissions: ['read'],
    });
    expect(cacheGetMock).toHaveBeenCalledWith(
      'user-1:00000000-0000-0000-0000-000000000000',
    );
  });

  it('should throw UnauthorizedException if workspace ID is missing', async () => {
    const payload = { sub: 'user-1' } as unknown as JwtPayload;
    const req = {
      headers: {},
    } as unknown as Request;

    await expect(strategy.validate(req, payload)).rejects.toThrow(
      UnauthorizedException,
    );
  });

  it('should throw UnauthorizedException if workspace ID is invalid UUID', async () => {
    const payload = { sub: 'user-1' } as unknown as JwtPayload;
    const req = {
      headers: {
        'x-workspace-id': 'invalid-uuid',
      },
    } as unknown as Request;

    await expect(strategy.validate(req, payload)).rejects.toThrow(
      UnauthorizedException,
    );
  });

  it('should fetch permissions and cache them if not cached', async () => {
    const payload = { sub: 'user-1' } as unknown as JwtPayload;
    const workspaceId = '00000000-0000-0000-0000-000000000000';
    const req = {
      headers: {
        'x-workspace-id': workspaceId,
        authorization: 'Bearer token',
      },
    } as unknown as Request;

    cacheGetMock.mockReturnValue(null);
    httpGetMock.mockReturnValue(of({ data: ['write'] }));

    const result = await strategy.validate(req, payload);

    expect(result.permissions).toEqual(['write']);
    expect(cacheSetMock).toHaveBeenCalledWith(
      'user-1:00000000-0000-0000-0000-000000000000',
      ['write'],
    );
  });

  it('should return empty permissions array if auth service fetch fails', async () => {
    const payload = { sub: 'user-1' } as unknown as JwtPayload;
    const req = {
      headers: {
        'x-workspace-id': '00000000-0000-0000-0000-000000000000',
        authorization: 'Bearer token',
      },
    } as unknown as Request;

    cacheGetMock.mockReturnValue(null);
    httpGetMock.mockReturnValue(throwError(() => new Error('Service down')));

    const result = await strategy.validate(req, payload);

    expect(result.permissions).toEqual([]);
  });
});
