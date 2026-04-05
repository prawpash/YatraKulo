import { DatabaseConfig } from '@app/shared/config/configuration';
import { Provider } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { Kysely, PostgresDialect } from 'kysely';
import { Pool } from 'pg';
import { DB } from './db';
import { DATABASE_CONNECTION } from './InjectionToken';

export const DatabaseConnection: Provider = {
  provide: DATABASE_CONNECTION,
  useFactory: (configService: ConfigService) => {
    const dbConfig = configService.get<DatabaseConfig>('database');

    const dialect = new PostgresDialect({
      pool: new Pool({
        database: dbConfig?.name ?? 'postgres',
        host: dbConfig?.host ?? 'localhost',
        port: dbConfig?.port ?? 5432,
        max: dbConfig?.maxConnections ?? 10,
        user: dbConfig?.username ?? 'postgres',
        password: dbConfig?.password ?? 'postgres',
      }),
    });

    return new Kysely<DB>({ dialect });
  },
  inject: [ConfigService],
};
