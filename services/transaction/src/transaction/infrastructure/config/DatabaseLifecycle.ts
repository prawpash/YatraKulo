import {
  Inject,
  Injectable,
  Logger,
  OnApplicationShutdown,
} from '@nestjs/common';
import { DATABASE_CONNECTION } from './InjectionToken';
import { Kysely } from 'kysely';
import { DB } from './db';

@Injectable()
export class DatabaseLifecycle implements OnApplicationShutdown {
  private readonly logger = new Logger(DatabaseLifecycle.name);

  constructor(
    @Inject(DATABASE_CONNECTION)
    private readonly db: Kysely<DB>,
  ) {}

  async onApplicationShutdown(signal?: string) {
    this.logger.log(
      `Shutting down database connection. Received signal: ${signal}`,
    );

    await this.db.destroy();
  }
}
