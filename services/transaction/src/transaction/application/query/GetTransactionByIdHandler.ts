import { Inject } from '@nestjs/common';
import { IQueryHandler, QueryHandler } from '@nestjs/cqrs';
import { Transaction } from '@app/transaction/domain/entity/Transaction';
import type { TransactionReadRepository } from '@app/transaction/domain/repository/TransactionReadRepository';
import { TRANSACTION_READ_REPOSITORY } from '@app/transaction/infrastructure/config/InjectionToken';
import { GetTransactionByIdQuery } from './GetTransactionByIdQuery';
import { NotFoundException } from '@yk/shared';

@QueryHandler(GetTransactionByIdQuery)
export class GetTransactionByIdHandler implements IQueryHandler<
  GetTransactionByIdQuery,
  Transaction
> {
  constructor(
    @Inject(TRANSACTION_READ_REPOSITORY)
    private readonly transactionReadRepository: TransactionReadRepository,
  ) {}

  async execute(query: GetTransactionByIdQuery): Promise<Transaction> {
    const transaction =
      await this.transactionReadRepository.findByIdAndWorkspaceId(
        query.id,
        query.workspaceId,
      );

    if (!transaction) {
      throw new NotFoundException(
        `Transaction with ID ${query.id} not found in workspace ${query.workspaceId}`,
      );
    }

    return transaction;
  }
}
