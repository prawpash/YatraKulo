import {
  PostgreSqlContainer,
  StartedPostgreSqlContainer,
} from '@testcontainers/postgresql';
import {
  RabbitMQContainer,
  StartedRabbitMQContainer,
} from '@testcontainers/rabbitmq';
import { Client } from 'pg';
import * as fs from 'fs';
import * as path from 'path';

export class TestcontainersSetup {
  private static pgContainer: StartedPostgreSqlContainer;
  private static rmqContainer: StartedRabbitMQContainer;

  static async start(): Promise<void> {
    try {
      this.pgContainer = await new PostgreSqlContainer('postgres:17.5-alpine3.22')
        .withDatabase('postgres')
        .withUsername('postgres')
        .withPassword('postgres')
        .start();

      this.rmqContainer = await new RabbitMQContainer(
        'rabbitmq:4.2.6-management-alpine',
      )
        .withExposedPorts(5672, 15672)
        .start();

      // Set environment variables for NestJS ConfigService
      process.env.DB_HOST = this.pgContainer.getHost();
      process.env.DB_PORT = this.pgContainer.getMappedPort(5432).toString();
      process.env.DB_USER = this.pgContainer.getUsername();
      process.env.DB_PASSWORD = this.pgContainer.getPassword();
      process.env.DB_NAME = this.pgContainer.getDatabase();

      const amqpUrl = this.rmqContainer.getAmqpUrl();
      process.env.RABBITMQ_URL = amqpUrl;

      await this.runMigrations();
    } catch (error) {
      await this.stop();
      throw error;
    }
  }

  static async stop(): Promise<void> {
    if (this.pgContainer) {
      await this.pgContainer.stop();
    }
    if (this.rmqContainer) {
      await this.rmqContainer.stop();
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
        '../../../../infra/db/transaction/migrations',
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
