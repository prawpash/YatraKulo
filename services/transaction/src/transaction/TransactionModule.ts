import { Module } from '@nestjs/common';
import { CqrsModule } from '@nestjs/cqrs';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { ConfigModule, ConfigService } from '@nestjs/config';

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
import { DeleteTransactionHandler } from './application/command/DeleteTransactionHandler';
import { OutboxRelayService } from './infrastructure/messaging/OutboxRelayService';
import { OutboxCleanupService } from './infrastructure/messaging/OutboxCleanupService';

@Module({
  imports: [
    CqrsModule,
    DatabaseModule,
    AuthModule,
    ClientsModule.registerAsync([
      {
        name: 'RABBITMQ_SERVICE',
        imports: [ConfigModule],
        useFactory: (configService: ConfigService) => ({
          transport: Transport.RMQ,
          options: {
            urls: [
              configService.get<string>(
                'RABBITMQ_URL',
                'amqp://localhost:5672',
              ),
            ],
            queue: 'transaction_queue',
            queueOptions: {
              durable: true,
            },
          },
        }),
        inject: [ConfigService],
      },
    ]),
  ],
  controllers: [TransactionController],
  providers: [
    CreateTransactionHandler,
    UpdateTransactionHandler,
    DeleteTransactionHandler,
    GetTransactionsHandler,
    GetTransactionByIdHandler,
    OutboxRelayService,
    OutboxCleanupService,
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
