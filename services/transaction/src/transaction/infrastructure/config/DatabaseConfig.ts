import { DatabaseConfig } from '@app/shared/config/configuration';
import { Provider } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { Kysely, PostgresDialect } from 'kysely';
import { Pool } from 'pg';
import { DATABASE_CONNECTION } from './InjectionToken';
import { DB } from './db';
import { metrics } from '@opentelemetry/api';

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

    const pool = new Pool({
      database: dbConfig.name,
      host: dbConfig.host,
      port: dbConfig.port,
      max: dbConfig?.maxConnections ?? 10,
      user: dbConfig.username,
      password: dbConfig.password,
    });

    const meter = metrics.getMeter('pg-pool');

    meter
      .createObservableGauge('pg.pool.connections', {
        description: 'Total number of connections in the pool',
      })
      .addCallback((result) => {
        result.observe(pool.totalCount);
      });

    meter
      .createObservableGauge('pg.pool.idle', {
        description: 'Number of idle connections in the pool',
      })
      .addCallback((result) => {
        result.observe(pool.idleCount);
      });

    meter
      .createObservableGauge('pg.pool.waiting', {
        description: 'Number of queued requests waiting for a connection',
      })
      .addCallback((result) => {
        result.observe(pool.waitingCount);
      });

    const dialect = new PostgresDialect({ pool });

    return new Kysely<DB>({ dialect });
  },
  inject: [ConfigService],
};
