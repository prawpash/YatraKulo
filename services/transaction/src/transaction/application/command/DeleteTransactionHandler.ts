import { Inject } from '@nestjs/common';
import { CommandHandler, ICommandHandler } from '@nestjs/cqrs';
import { v4 as uuidv4 } from 'uuid';
import { NotFoundException } from '@yk/shared';
import type { TransactionReadRepository } from '@app/transaction/domain/repository/TransactionReadRepository';
import type { TransactionWriteRepository } from '@app/transaction/domain/repository/TransactionWriteRepository';
import {
  TRANSACTION_READ_REPOSITORY,
  TRANSACTION_WRITE_REPOSITORY,
} from '@app/transaction/infrastructure/config/InjectionToken';
import { DeleteTransactionCommand } from './DeleteTransactionCommand';

@CommandHandler(DeleteTransactionCommand)
export class DeleteTransactionHandler implements ICommandHandler<DeleteTransactionCommand> {
  constructor(
    @Inject(TRANSACTION_READ_REPOSITORY)
    private readonly transactionReadRepository: TransactionReadRepository,
    @Inject(TRANSACTION_WRITE_REPOSITORY)
    private readonly transactionWriteRepository: TransactionWriteRepository,
  ) {}

  async execute(command: DeleteTransactionCommand): Promise<void> {
    const transaction =
      await this.transactionReadRepository.findByIdAndWorkspaceId(
        command.id,
        command.workspaceId,
      );

    if (!transaction || transaction.createdBy !== command.deletedBy) {
      throw new NotFoundException(
        `Transaction with ID ${command.id} not found in workspace ${command.workspaceId}`,
      );
    }

    const now = new Date();
    transaction.delete(now, command.deletedBy, uuidv4(), now);

    await this.transactionWriteRepository.save(transaction);
  }
}
