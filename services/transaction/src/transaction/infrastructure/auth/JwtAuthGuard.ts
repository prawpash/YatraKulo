import { Injectable, Logger, UnauthorizedException } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

@Injectable()
export class JwtAuthGuard extends AuthGuard('jwt') {
  private readonly logger = new Logger(JwtAuthGuard.name);

  handleRequest(err: any, user: any) {
    if (err || !user) {
      if (err instanceof Error) {
        const reason = err?.message ?? 'Unknown auth error';

        this.logger.warn(`Auth failed: ${reason}`);

        throw new UnauthorizedException(reason);
      }

      this.logger.error(err);

      throw new UnauthorizedException('Unknown auth error');
    }

    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return user;
  }
}
