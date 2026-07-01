import { Module, Global } from '@nestjs/common';
import { GatewayAuthGuard } from './GatewayAuthGuard';
import { PermissionsGuard } from './PermissionsGuard';

@Global()
@Module({
  providers: [GatewayAuthGuard, PermissionsGuard],
  exports: [GatewayAuthGuard, PermissionsGuard],
})
export class AuthModule {}
