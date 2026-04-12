import { Module } from '@nestjs/common';
import { PassportModule } from '@nestjs/passport';
import { HttpModule } from '@nestjs/axios';
import { JwtStrategy } from './JwtStrategy';
import { JwtAuthGuard } from './JwtAuthGuard';
import { PermissionsGuard } from './PermissionsGuard';
import { PermissionsCache } from './PermissionsCache';

@Module({
  imports: [PassportModule.register({ defaultStrategy: 'jwt' }), HttpModule],
  providers: [JwtStrategy, JwtAuthGuard, PermissionsGuard, PermissionsCache],
  exports: [JwtAuthGuard, PermissionsGuard],
})
export class AuthModule {}
