import {
  CanActivate,
  ExecutionContext,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { AppLogger } from '@yk/shared';
import { Request } from 'express';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class GatewayAuthGuard implements CanActivate {
  private readonly logger = new AppLogger(GatewayAuthGuard.name);

  constructor(private configService: ConfigService) {}

  canActivate(context: ExecutionContext): boolean {
    const request = context.switchToHttp().getRequest<Request>();
    
    // Check if the request came through Kong by validating the secret header
    // (In local dev, you might skip this if the secret is not set, but good for production)
    const expectedSecret = this.configService.get<string>('KONG_SHARED_SECRET');
    const incomingSecret = request.header('x-kong-secret');

    if (expectedSecret && incomingSecret !== expectedSecret) {
      this.logger.warn('Invalid or missing X-Kong-Secret header');
      throw new UnauthorizedException('Direct access to microservice is forbidden');
    }

    const userId = request.header('x-user-id');
    if (!userId) {
      this.logger.warn('Missing X-User-Id header');
      throw new UnauthorizedException('User ID is missing');
    }

    const permissionsHeader = request.header('x-user-permissions') || '';
    const permissions = permissionsHeader.split(',').filter(Boolean);

    // Attach to request object so PermissionsGuard can use it
    request['user'] = {
      sub: userId,
      permissions: permissions,
    };

    return true;
  }
}
