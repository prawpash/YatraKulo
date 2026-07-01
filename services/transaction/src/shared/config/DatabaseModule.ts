import { Module, Global } from '@nestjs/common';
import { DatabaseConnection } from './DatabaseConfig';
import { DATABASE_CONNECTION } from './InjectionToken';
import { DatabaseLifecycle } from './DatabaseLifecycle';

@Global()
@Module({
  providers: [DatabaseConnection, DatabaseLifecycle],
  exports: [DATABASE_CONNECTION],
})
export class DatabaseModule {}
