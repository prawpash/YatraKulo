import { Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { passportJwtSecret } from 'jwks-rsa';
import { HttpService } from '@nestjs/axios';
import { firstValueFrom, timer } from 'rxjs';
import { timeout, retry } from 'rxjs/operators';
import { Request } from 'express';
import { AxiosError } from 'axios';

export interface JwtPayload {
  sub: string;
  aud: string;
  nbf: number;
  scope: string[];
  iss: string;
  exp: number;
  iat: number;
  jti: string;
}

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy, 'jwt') {
  private readonly logger = new Logger(JwtStrategy.name);

  constructor(
    private readonly configService: ConfigService,
    private readonly httpService: HttpService,
  ) {
    const jwksUri = configService.get<string>('auth.jwks_uri')!;
    const jwksLogger = new Logger('JWKS');

    super({
      secretOrKeyProvider: passportJwtSecret({
        cache: true,
        rateLimit: true,
        jwksRequestsPerMinute: 5,
        jwksUri: jwksUri,
        handleSigningKeyError(err, cb) {
          if (err) {
            jwksLogger.error(err);
          }
          cb(err);
        },
      }),
      issuer: 'http://localhost:5000',
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      algorithms: ['RS256'],
      ignoreExpiration: false,
      passReqToCallback: true,
    });
  }

  async onModuleInit() {
    // this.logger.debug('Creating JWT Strategy');
    const jwksUri = this.configService.get<string>('auth.jwks_uri')!;

    try {
      const res = await fetch(jwksUri);
      // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
      const jwks = (await res.json()) ?? {};
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      this.logger.log(`JWKS: reachable, ${jwks?.keys?.length ?? 0} keys found`);
    } catch (err) {
      if (err instanceof Error) {
        this.logger.error(
          `JWKS: UNREACHABLE at startup — ${jwksUri}. Error: ${err.message}`,
        );
      } else {
        console.log(err);
      }
    }
  }

  async validate(req: Request, payload: JwtPayload) {
    const authServiceUrl = this.configService.get<string>('auth.service_url');

    const { sub: userId } = payload;

    const workspaceId = req.headers['x-workspace-id'];

    if (!workspaceId) {
      this.logger.warn(
        `No workspace ID found in request headers, skipping permissions check for user ${userId}`,
      );
      return {
        ...payload,
        permissions: [],
      };
    }

    // malformed workspace ID
    if (Array.isArray(workspaceId)) {
      this.logger.warn(
        `Malformed workspace ID found in request headers, skipping permissions check for user ${userId}`,
      );
      return {
        ...payload,
        permissions: [],
      };
    }

    const authHeader = req.headers.authorization;

    // TODO: Implement circuit breaker for this network call in the future
    try {
      this.logger.debug(
        `Fetching permissions for user ${userId} in workspace ${workspaceId} from ${authServiceUrl}`,
      );

      const response = await firstValueFrom(
        this.httpService
          .get<string[]>(
            `${authServiceUrl}/api/v1/workspaces/${workspaceId}/members/${userId}/permissions`,
            {
              headers: {
                Authorization: authHeader,
              },
            },
          )
          .pipe(
            timeout(1000),
            retry({
              count: 2,
              delay: (error: AxiosError) => {
                const status = error.response?.status;
                const isTransient4xx = status === 429 || status === 408;

                if (
                  status &&
                  status >= 400 &&
                  status < 500 &&
                  !isTransient4xx
                ) {
                  this.logger.warn(
                    `Client error ${status} fetching permissions for user ${userId}, not retrying`,
                  );
                  throw error; // Abort retry
                }

                this.logger.debug(
                  `Retrying permissions fetch for user ${userId} due to error: ${error.message}`,
                );
                return timer(1000); // Retry after 1s
              },
            }),
          ),
      );

      this.logger.debug('Result from fetch the permissions', {
        payload,
        permissions: response.data,
      });

      return {
        ...payload,
        permissions: response.data,
      };
    } catch (error: unknown) {
      const errorMessage =
        error instanceof Error ? error.message : 'Unknown error';
      this.logger.error(
        `Failed to fetch permissions for user ${userId} with workspace ${workspaceId?.toString()}: ${errorMessage}`,
      );
      // Even if permissions fetch fails, we return the payload (authenticated user)
      // but without permissions. The guards will handle missing permissions.
      return {
        ...payload,
        permissions: [],
      };
    }
  }
}
