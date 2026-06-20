import { Inject, Injectable, Logger } from '@nestjs/common';
import { Cron, CronExpression } from '@nestjs/schedule';
import { ClientProxy } from '@nestjs/microservices';
import { Kysely } from 'kysely';
import { lastValueFrom } from 'rxjs';
import { DATABASE_CONNECTION } from '@app/transaction/infrastructure/config/InjectionToken';
import { DB } from '@app/transaction/infrastructure/config/db';
import { OutboxStatus } from './OutboxStatus';
import { trace, SpanStatusCode } from '@opentelemetry/api';

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
    const tracer = trace.getTracer('transaction-service');
    await tracer.startActiveSpan(
      'OutboxRelayService.handleCron',
      async (span) => {
        try {
          const pendingEvents = await this.db
            .selectFrom('outbox')
            .selectAll()
            .where('status', '=', OutboxStatus.PENDING)
            .orderBy('created_at', 'asc')
            .limit(100)
            .execute();

          if (pendingEvents.length === 0) {
            span.setStatus({ code: SpanStatusCode.OK });
            span.end();
            return;
          }

          this.logger.log(
            `Processing ${pendingEvents.length} pending outbox events`,
          );
          span.setAttribute('events.count', pendingEvents.length);

          for (const event of pendingEvents) {
            try {
              const payload = event.payload;
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
              span.recordException(error as Error);

              await this.db
                .updateTable('outbox')
                .set({ status: OutboxStatus.FAILED })
                .where('id', '=', event.id)
                .execute();
            }
          }
          span.setStatus({ code: SpanStatusCode.OK });
        } catch (error) {
          const errorMessage =
            error instanceof Error ? error.message : String(error);
          this.logger.error(`Outbox relay operation failed: ${errorMessage}`);
          span.recordException(error as Error);
          span.setStatus({
            code: SpanStatusCode.ERROR,
            message: String(error),
          });
        } finally {
          span.end();
        }
      },
    );
  }
}
