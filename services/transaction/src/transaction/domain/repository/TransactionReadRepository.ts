import { Transaction } from '@app/transaction/domain/entity/Transaction';
import { DomainPage, DomainPageRequest } from '@yk/shared';

export interface GetTransactionsParams {
  workspaceId: string;
  fromAccountId?: string;
  toAccountId?: string;
  pageRequest: DomainPageRequest;
}

export interface TransactionReadRepository {
  findById(id: string): Promise<Transaction | null>;

  findByIdAndWorkspaceId(
    id: string,
    workspaceId: string,
  ): Promise<Transaction | null>;

  findByIdempotencyKeyAndWorkspaceId(
    idempotencyKey: string,
    workspaceId: string,
  ): Promise<Transaction | null>;

  getTransactions(
    params: GetTransactionsParams,
  ): Promise<DomainPage<Transaction>>;
}
