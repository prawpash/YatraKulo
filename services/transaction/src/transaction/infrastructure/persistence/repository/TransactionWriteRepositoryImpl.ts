import { Inject, Injectable } from '@nestjs/common';
import { Kysely } from 'kysely';
import { Transaction } from '@app/transaction/domain/entity/Transaction';
import { TransactionWriteRepository } from '@app/transaction/domain/repository/TransactionWriteRepository';
import { DATABASE_CONNECTION } from '@app/transaction/infrastructure/config/InjectionToken';
import { DB, Json } from '@app/transaction/infrastructure/config/db';
import { TransactionMapper } from '@app/transaction/infrastructure/persistence/mapper/TransactionMapper';
import { IDomainEvent } from '@app/transaction/domain/events/IDomainEvent';

@Injectable()
export class TransactionWriteRepositoryImpl implements TransactionWriteRepository {
  constructor(
    @Inject(DATABASE_CONNECTION)
    private readonly db: Kysely<DB>,
  ) {}

  async save(transaction: Transaction): Promise<Transaction> {
    const persistence = TransactionMapper.toPersistence(transaction);
    const events = transaction.uncommittedEvents;

    await this.db.transaction().execute(async (trx) => {
      await trx
        .insertInto('transaction')
        .values(persistence)
        .onConflict((oc) =>
          oc.column('idempotency_key').doUpdateSet({
            amount: persistence.amount,
            note: persistence.note,
            from_account_id: persistence.from_account_id,
            to_account_id: persistence.to_account_id,
            workspace_id: persistence.workspace_id,
            updated_at: persistence.updated_at,
            updated_by: persistence.updated_by,
            deleted_at: persistence.deleted_at,
            deleted_by: persistence.deleted_by,
          }),
        )
        .execute();

      if (events.length > 0) {
        const outboxRecords = events.map((event: IDomainEvent) => ({
          aggregate_id: transaction.id,
          aggregate_type: 'Transaction',
          event_type: event.eventType,
          payload: event as unknown as Json,
        }));

        await trx.insertInto('outbox').values(outboxRecords).execute();
      }
    });

    transaction.clearEvents();
    return transaction;
  }
}
