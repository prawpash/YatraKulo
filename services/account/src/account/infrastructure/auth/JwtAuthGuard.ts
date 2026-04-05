import { Injectable, Logger, UnauthorizedException } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

@Injectable()
export class JwtAuthGuard extends AuthGuard('jwt') {
  private readonly logger = new Logger(JwtAuthGuard.name);

  handleRequest(err: any, user: any, info: any) {
    if (err || !user) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment, @typescript-eslint/no-unsafe-member-access
      const reason = info?.message ?? 'Unknown auth error';
      this.logger.warn(`Auth failed: ${reason}`);
      throw new UnauthorizedException(reason);
    }

    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return user;
  }
}
