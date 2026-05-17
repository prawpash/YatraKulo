import { DatabaseConfig } from '@app/shared/config/configuration';
import { Provider } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { Kysely, PostgresDialect } from 'kysely';
import { Pool } from 'pg';
import { DATABASE_CONNECTION } from './InjectionToken';
import { DB } from './db';

export const DatabaseConnection: Provider = {
  provide: DATABASE_CONNECTION,
  useFactory: (configService: ConfigService) => {
    const dbConfig = configService.get<DatabaseConfig>('database');

    if (!dbConfig) {
      throw new Error('Database config is not defined');
    }

    if (
      !dbConfig.host ||
      !dbConfig.port ||
      !dbConfig.username ||
      !dbConfig.password ||
      !dbConfig.name
    ) {
      throw new Error('Incomplete database config');
    }

    const dialect = new PostgresDialect({
      pool: new Pool({
        database: dbConfig.name,
        host: dbConfig.host,
        port: dbConfig.port,
        max: dbConfig?.maxConnections ?? 10,
        user: dbConfig.username,
        password: dbConfig.password,
      }),
    });

    return new Kysely<DB>({ dialect });
  },
  inject: [ConfigService],
};
