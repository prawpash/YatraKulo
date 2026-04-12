import { Module } from '@nestjs/common';
import { DatabaseConnection } from './DatabaseConfig';
import { DATABASE_CONNECTION } from './InjectionToken';
import { DatabaseLifecycle } from './DatabaseLifecycle';

@Module({
  providers: [DatabaseConnection, DatabaseLifecycle],
  exports: [DATABASE_CONNECTION],
})
export class DatabaseModule {}
