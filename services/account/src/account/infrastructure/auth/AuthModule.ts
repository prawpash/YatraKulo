import { Module } from '@nestjs/common';
import { PassportModule } from '@nestjs/passport';
import { HttpModule } from '@nestjs/axios';
import { JwtStrategy } from './JwtStrategy';
import { JwtAuthGuard } from './JwtAuthGuard';
import { PermissionsCache } from './PermissionsCache';

@Module({
  imports: [PassportModule.register({ defaultStrategy: 'jwt' }), HttpModule],
  providers: [JwtStrategy, JwtAuthGuard, PermissionsCache],
  exports: [JwtAuthGuard],
})
export class AuthModule {}
