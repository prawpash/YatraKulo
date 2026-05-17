import { Inject } from '@nestjs/common';
import { CommandHandler, ICommandHandler } from '@nestjs/cqrs';
import { v4 as uuidv4 } from 'uuid';
import { TransactionBuilder } from '@app/transaction/domain/entity/Transaction';
import type { TransactionReadRepository } from '@app/transaction/domain/repository/TransactionReadRepository';
import type { TransactionWriteRepository } from '@app/transaction/domain/repository/TransactionWriteRepository';
import {
  TRANSACTION_READ_REPOSITORY,
  TRANSACTION_WRITE_REPOSITORY,
} from '@app/transaction/infrastructure/config/InjectionToken';
import { CreateTransactionCommand } from './CreateTransactionCommand';

@CommandHandler(CreateTransactionCommand)
export class CreateTransactionHandler implements ICommandHandler<CreateTransactionCommand> {
  constructor(
    @Inject(TRANSACTION_READ_REPOSITORY)
    private readonly transactionReadRepository: TransactionReadRepository,
    @Inject(TRANSACTION_WRITE_REPOSITORY)
    private readonly transactionWriteRepository: TransactionWriteRepository,
  ) {}

  async execute(command: CreateTransactionCommand) {
    const existingTransaction =
      await this.transactionReadRepository.findByIdempotencyKeyAndWorkspaceId(
        command.idempotencyKey,
        command.workspaceId,
      );

    if (existingTransaction) {
      return existingTransaction;
    }

    const now = new Date();

    const transaction = new TransactionBuilder()
      .withId(uuidv4())
      .withWorkspaceId(command.workspaceId)
      .withAmount(command.amount)
      .withNote(command.note)
      .withFromAccountId(command.fromAccountId)
      .withToAccountId(command.toAccountId)
      .withIdempotencyKey(command.idempotencyKey)
      .withCreatedAt(now)
      .withUpdatedAt(now)
      .withCreatedBy(command.createdBy)
      .withUpdatedBy(command.createdBy)
      .build(uuidv4(), now);

    return this.transactionWriteRepository.save(transaction);
  }
}
