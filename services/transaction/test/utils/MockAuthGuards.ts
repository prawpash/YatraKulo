import { JwtPayload } from '@app/transaction/infrastructure/auth/JwtStrategy';
import { CanActivate, ExecutionContext, Injectable } from '@nestjs/common';

@Injectable()
export class MockJwtAuthGuard implements CanActivate {
  canActivate(context: ExecutionContext): boolean {
    const req = context.switchToHttp().getRequest<{
      user: Pick<JwtPayload, 'sub'> & { permissions?: string[] };
    }>();

    // Standard mock user with all common permissions for testing
    if (!req.user) {
      req.user = {
        sub: '00000000-0000-0000-0000-000000000000',
        permissions: [
          'TRANSACTION_READ',
          'TRANSACTION_WRITE',
          'TRANSACTION_UPDATE',
          'TRANSACTION_DELETE',
        ],
      };
    }

    return true;
  }
}

@Injectable()
export class MockPermissionsGuard implements CanActivate {
  canActivate(): boolean {
    // By default, we allow all requests in integration tests.
    // If you need to test permission rejection, you can override this guard per test.
    return true;
  }
}
