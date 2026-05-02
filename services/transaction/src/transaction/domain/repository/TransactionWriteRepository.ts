import { Transaction } from '@app/transaction/domain/entity/Transaction';

export interface TransactionWriteRepository {
  save(transaction: Transaction): Promise<Transaction>;
}
