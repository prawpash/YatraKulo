import { Inject } from '@nestjs/common';
import { CommandHandler, ICommandHandler } from '@nestjs/cqrs';
import type { TransactionReadRepository } from '@app/transaction/domain/repository/TransactionReadRepository';
import type { TransactionWriteRepository } from '@app/transaction/domain/repository/TransactionWriteRepository';
import {
  TRANSACTION_READ_REPOSITORY,
  TRANSACTION_WRITE_REPOSITORY,
} from '@app/transaction/infrastructure/config/InjectionToken';
import { UpdateTransactionCommand } from './UpdateTransactionCommand';
import { NotFoundException } from '@yk/shared';

@CommandHandler(UpdateTransactionCommand)
export class UpdateTransactionHandler implements ICommandHandler<UpdateTransactionCommand> {
  constructor(
    @Inject(TRANSACTION_READ_REPOSITORY)
    private readonly transactionReadRepository: TransactionReadRepository,
    @Inject(TRANSACTION_WRITE_REPOSITORY)
    private readonly transactionWriteRepository: TransactionWriteRepository,
  ) {}

  async execute(command: UpdateTransactionCommand): Promise<void> {
    const transaction =
      await this.transactionReadRepository.findByIdAndWorkspaceId(
        command.id,
        command.workspaceId,
      );

    if (!transaction) {
      throw new NotFoundException(
        `Transaction with ID ${command.id} not found in workspace ${command.workspaceId}`,
      );
    }

    transaction.update({
      amount: command.amount,
      note: command.note,
      updatedAt: new Date(),
      updatedBy: command.updatedBy,
    });

    await this.transactionWriteRepository.save(transaction);
  }
}
