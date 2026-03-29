import { Module } from '@nestjs/common';
import { DatabaseConnection } from './DatabaseConfig';
import { DATABASE_CONNECTION } from './InjectionToken';

@Module({
  providers: [DatabaseConnection],
  exports: [DATABASE_CONNECTION],
})
export class DatabaseModule {}
