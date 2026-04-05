import { Module } from '@nestjs/common';
import { PassportModule } from '@nestjs/passport';
import { HttpModule } from '@nestjs/axios';
import { JwtStrategy } from './JwtStrategy';
import { JwtAuthGuard } from './JwtAuthGuard';

@Module({
  imports: [PassportModule.register({ defaultStrategy: 'jwt' }), HttpModule],
  providers: [JwtStrategy, JwtAuthGuard],
  exports: [JwtAuthGuard],
})
export class AuthModule {}
