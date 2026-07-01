import { Inject } from '@nestjs/common';
import { IQueryHandler, QueryHandler } from '@nestjs/cqrs';
import { DomainPage } from '@yk/shared';
import { Transaction } from '@app/transaction/domain/entity/Transaction';
import type { TransactionReadRepository } from '@app/transaction/domain/repository/TransactionReadRepository';
import { TRANSACTION_READ_REPOSITORY } from '@app/shared/config/InjectionToken';
import { GetTransactionsQuery } from './GetTransactionsQuery';

@QueryHandler(GetTransactionsQuery)
export class GetTransactionsHandler implements IQueryHandler<
  GetTransactionsQuery,
  DomainPage<Transaction>
> {
  constructor(
    @Inject(TRANSACTION_READ_REPOSITORY)
    private readonly transactionReadRepository: TransactionReadRepository,
  ) {}

  async execute(query: GetTransactionsQuery): Promise<DomainPage<Transaction>> {
    return this.transactionReadRepository.getTransactions({
      workspaceId: query.workspaceId,
      pageRequest: query.pageRequest,
      fromAccountId: query.fromAccountId,
      toAccountId: query.toAccountId,
    });
  }
}
