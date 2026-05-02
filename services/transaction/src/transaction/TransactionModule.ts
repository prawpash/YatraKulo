import { Module } from '@nestjs/common';
import { CqrsModule } from '@nestjs/cqrs';

// Repository implementations
import { TransactionWriteRepositoryImpl } from './infrastructure/persistence/repository/TransactionWriteRepositoryImpl';
import { TransactionReadRepositoryImpl } from './infrastructure/persistence/repository/TransactionReadRepositoryImpl';

// Infrastructure modules
import { DatabaseModule } from './infrastructure/config/DatabaseModule';
import { AuthModule } from './infrastructure/auth/AuthModule';

// Injection tokens
import {
  TRANSACTION_WRITE_REPOSITORY,
  TRANSACTION_READ_REPOSITORY,
} from './infrastructure/config/InjectionToken';
import { TransactionController } from './interfaces/http/TransactionController';
import { CreateTransactionHandler } from './application/command/CreateTransactionHandler';
import { UpdateTransactionHandler } from './application/command/UpdateTransactionHandler';
import { GetTransactionsHandler } from './application/query/GetTransactionsHandler';
import { GetTransactionByIdHandler } from './application/query/GetTransactionByIdHandler';

@Module({
  imports: [CqrsModule, DatabaseModule, AuthModule],
  controllers: [TransactionController],
  providers: [
    CreateTransactionHandler,
    UpdateTransactionHandler,
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
