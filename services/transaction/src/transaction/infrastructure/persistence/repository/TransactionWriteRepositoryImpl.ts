import { Inject, Injectable } from '@nestjs/common';
import { Kysely } from 'kysely';
import { Transaction } from '@app/transaction/domain/entity/Transaction';
import { TransactionWriteRepository } from '@app/transaction/domain/repository/TransactionWriteRepository';
import { DATABASE_CONNECTION } from '@app/transaction/infrastructure/config/InjectionToken';
import { DB } from '@app/transaction/infrastructure/config/db';
import { TransactionMapper } from '@app/transaction/infrastructure/persistence/mapper/TransactionMapper';

@Injectable()
export class TransactionWriteRepositoryImpl implements TransactionWriteRepository {
  constructor(
    @Inject(DATABASE_CONNECTION)
    private readonly db: Kysely<DB>,
  ) {}

  async save(transaction: Transaction): Promise<Transaction> {
    const persistence = TransactionMapper.toPersistence(transaction);

    await this.db
      .insertInto('transaction')
      .values(persistence)
      .onConflict((oc) =>
        oc.column('id').doUpdateSet({
          amount: persistence.amount,
          note: persistence.note,
          from_account_id: persistence.from_account_id,
          to_account_id: persistence.to_account_id,
          idempotency_key: persistence.idempotency_key,
          workspace_id: persistence.workspace_id,
          updated_at: persistence.updated_at,
          updated_by: persistence.updated_by,
          deleted_at: persistence.deleted_at,
          deleted_by: persistence.deleted_by,
        }),
      )
      .execute();

    return transaction;
  }
}
