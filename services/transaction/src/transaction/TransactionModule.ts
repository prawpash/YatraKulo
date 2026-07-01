import { Module } from '@nestjs/common';
import { CqrsModule } from '@nestjs/cqrs';

// Repository implementations
import { TransactionWriteRepositoryImpl } from './infrastructure/persistence/repository/TransactionWriteRepositoryImpl';
import { TransactionReadRepositoryImpl } from './infrastructure/persistence/repository/TransactionReadRepositoryImpl';

// Injection tokens
import {
  TRANSACTION_WRITE_REPOSITORY,
  TRANSACTION_READ_REPOSITORY,
} from '@app/shared/config/InjectionToken';
import { TransactionController } from './interfaces/http/TransactionController';
import { CreateTransactionHandler } from './application/command/CreateTransactionHandler';
import { UpdateTransactionHandler } from './application/command/UpdateTransactionHandler';
import { GetTransactionsHandler } from './application/query/GetTransactionsHandler';
import { GetTransactionByIdHandler } from './application/query/GetTransactionByIdHandler';
import { DeleteTransactionHandler } from './application/command/DeleteTransactionHandler';

@Module({
  imports: [
    CqrsModule,
  ],
  controllers: [TransactionController],
  providers: [
    CreateTransactionHandler,
    UpdateTransactionHandler,
    DeleteTransactionHandler,
    GetTransactionsHandler,
    GetTransactionByIdHandler,
    {
      provide: TRANSACTION_WRITE_REPOSITORY,
      useClass: TransactionWriteRepositoryImpl,
    },
    {
      provide: TRANSACTION_READ_REPOSITORY,
      useClass: TransactionReadRepositoryImpl,
    },
  ],
  exports: [TRANSACTION_WRITE_REPOSITORY, TRANSACTION_READ_REPOSITORY],
})
export class TransactionModule {}
