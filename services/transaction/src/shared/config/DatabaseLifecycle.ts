import {
  Inject,
  Injectable,
  OnApplicationShutdown,
} from '@nestjs/common';
import { AppLogger } from '@yk/shared';
import { DATABASE_CONNECTION } from './InjectionToken';
import { Kysely } from 'kysely';
import { DB } from './db';

@Injectable()
export class DatabaseLifecycle implements OnApplicationShutdown {
  private readonly logger = new AppLogger(DatabaseLifecycle.name);

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
