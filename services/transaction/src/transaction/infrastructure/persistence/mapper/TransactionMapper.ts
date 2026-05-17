import {
  Transaction,
  TransactionBuilder,
} from '@app/transaction/domain/entity/Transaction';
import { Selectable } from 'kysely';
import { TransactionPersistence } from '@app/transaction/infrastructure/config/db';

export class TransactionMapper {
  static toDomain(row: Selectable<TransactionPersistence>): Transaction {
    return new TransactionBuilder()
      .withId(row.id)
      .withAmount(Number(row.amount))
      .withNote(row.note)
      .withFromAccountId(row.from_account_id)
      .withToAccountId(row.to_account_id)
      .withIdempotencyKey(row.idempotency_key)
      .withWorkspaceId(row.workspace_id)
      .withCreatedAt(row.created_at as Date)
      .withUpdatedAt(row.updated_at as Date)
      .withDeletedAt(row.deleted_at as Date)
      .withCreatedBy(row.created_by)
      .withUpdatedBy(row.updated_by)
      .withDeletedBy(row.deleted_by)
      .reconstitute();
  }

  static toPersistence(transaction: Transaction) {
    return {
      id: transaction.id,
      amount: transaction.amount,
      note: transaction.note,
      from_account_id: transaction.fromAccountId,
      to_account_id: transaction.toAccountId,
      idempotency_key: transaction.idempotencyKey,
      workspace_id: transaction.workspaceId,
      created_at: transaction.createdAt,
      updated_at: transaction.updatedAt,
      deleted_at: transaction.deletedAt,
      created_by: transaction.createdBy,
      updated_by: transaction.updatedBy,
      deleted_by: transaction.deletedBy,
    };
  }
}
