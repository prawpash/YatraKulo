import { Inject, Injectable, Logger } from '@nestjs/common';
import { Cron, CronExpression } from '@nestjs/schedule';
import { ClientProxy } from '@nestjs/microservices';
import { Kysely } from 'kysely';
import { lastValueFrom } from 'rxjs';
import { DATABASE_CONNECTION } from '@app/transaction/infrastructure/config/InjectionToken';
import { DB } from '@app/transaction/infrastructure/config/db';
import { OutboxStatus } from './OutboxStatus';

@Injectable()
export class OutboxRelayService {
  private readonly logger = new Logger(OutboxRelayService.name);

  constructor(
    @Inject(DATABASE_CONNECTION)
    private readonly db: Kysely<DB>,
    @Inject('RABBITMQ_SERVICE')
    private readonly client: ClientProxy,
  ) {}

  @Cron(CronExpression.EVERY_10_SECONDS)
  async handleCron() {
    const pendingEvents = await this.db
      .selectFrom('outbox')
      .selectAll()
      .where('status', '=', OutboxStatus.PENDING)
      .orderBy('created_at', 'asc')
      .limit(100)
      .execute();

    if (pendingEvents.length === 0) return;

    this.logger.log(`Processing ${pendingEvents.length} pending outbox events`);

    for (const event of pendingEvents) {
      try {
        const payload = event.payload;
        // Using emit for event-driven (fire and forget from application perspective,
        // but Outbox pattern ensures it reaches the broker).
        // NestJS ClientProxy.emit returns an Observable.
        // We use lastValueFrom to ensure the operation completes.
        await lastValueFrom(this.client.emit(event.event_type, payload));

        await this.db
          .updateTable('outbox')
          .set({
            status: OutboxStatus.PUBLISHED,
            published_at: new Date(),
          })
          .where('id', '=', event.id)
          .execute();
      } catch (error) {
        const errorMessage =
          error instanceof Error ? error.message : String(error);
        this.logger.error(
          `Failed to publish event ${event.id}: ${errorMessage}`,
        );
        await this.db
          .updateTable('outbox')
          .set({ status: OutboxStatus.FAILED })
          .where('id', '=', event.id)
          .execute();
      }
    }
  }
}
