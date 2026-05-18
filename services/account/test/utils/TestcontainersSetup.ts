import {
  PostgreSqlContainer,
  StartedPostgreSqlContainer,
} from '@testcontainers/postgresql';
import { Client } from 'pg';
import * as fs from 'fs';
import * as path from 'path';

export class TestcontainersSetup {
  private static container: StartedPostgreSqlContainer;

  static async start(): Promise<string> {
    this.container = await new PostgreSqlContainer('postgres:17.5-alpine3.22')
      .withDatabase('postgres')
      .withUsername('postgres')
      .withPassword('postgres')
      .start();

    const connectionUri = this.container.getConnectionUri();

    // Set environment variables for NestJS ConfigService
    process.env.DB_HOST = this.container.getHost();
    process.env.DB_PORT = this.container.getMappedPort(5432).toString();
    process.env.DB_USER = this.container.getUsername();
    process.env.DB_PASSWORD = this.container.getPassword();
    process.env.DB_NAME = this.container.getDatabase();

    await this.runMigrations();

    return connectionUri;
  }

  static async stop(): Promise<void> {
    if (this.container) {
      await this.container.stop();
    }
  }

  private static async runMigrations(): Promise<void> {
    const client = new Client({
      host: process.env.DB_HOST,
      port: parseInt(process.env.DB_PORT || '5432', 10),
      user: process.env.DB_USER,
      password: process.env.DB_PASSWORD,
      database: process.env.DB_NAME,
    });

    try {
      await client.connect();

      const migrationsDir = path.join(
        __dirname,
        '../../../../infra/db/account/migrations',
      );

      if (!fs.existsSync(migrationsDir)) {
        throw new Error(`Migrations directory not found: ${migrationsDir}`);
      }

      const files = fs
        .readdirSync(migrationsDir)
        .filter((f) => f.endsWith('.sql'))
        .sort((a, b) =>
          a.localeCompare(b, undefined, { numeric: true, sensitivity: 'base' }),
        );

      for (const file of files) {
        const filePath = path.join(migrationsDir, file);
        const sql = fs.readFileSync(filePath, 'utf8');
        await client.query(sql);
      }
    } finally {
      await client.end();
    }
  }
}
