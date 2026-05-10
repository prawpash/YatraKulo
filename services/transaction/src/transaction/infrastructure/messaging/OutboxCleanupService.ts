import { Inject, Injectable, Logger } from '@nestjs/common';
import { Cron, CronExpression } from '@nestjs/schedule';
import { ConfigService } from '@nestjs/config';
import { Kysely } from 'kysely';
import { DATABASE_CONNECTION } from '@app/transaction/infrastructure/config/InjectionToken';
import { DB } from '@app/transaction/infrastructure/config/db';
import { OutboxStatus } from './OutboxStatus';

@Injectable()
export class OutboxCleanupService {
  private readonly logger = new Logger(OutboxCleanupService.name);

  constructor(
    @Inject(DATABASE_CONNECTION)
    private readonly db: Kysely<DB>,
    private readonly configService: ConfigService,
  ) {}

  @Cron(CronExpression.EVERY_DAY_AT_MIDNIGHT)
  async handleCron() {
    const retentionDays = Number(
      this.configService.get<number>('OUTBOX_RETENTION_DAYS', 7),
    );
    const thresholdDate = new Date();
    thresholdDate.setDate(thresholdDate.getDate() - retentionDays);

    const result = await this.db
      .deleteFrom('outbox')
      .where('status', '=', OutboxStatus.PUBLISHED)
      .where('published_at', '<', thresholdDate)
      .executeTakeFirst();

    this.logger.log(
      `Cleaned up ${result.numDeletedRows} published outbox events (Retention: ${retentionDays} days)`,
    );
  }
}
