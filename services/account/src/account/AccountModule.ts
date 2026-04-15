import { Module } from '@nestjs/common';
import { CqrsModule } from '@nestjs/cqrs';

// Controllers
import { AccountController } from './interfaces/http/AccountController';

// Command handlers
import { CreateAccountHandler } from './application/command/CreateAccountHandler';
import { UpdateAccountHandler } from './application/command/UpdateAccountHandler';
import { DeleteAccountHandler } from './application/command/DeleteAccountHandler';

// Query handlers
import { GetAccountsHandler } from './application/query/GetAccountsHandler';
import { GetAccountByIdHandler } from './application/query/GetAccountByIdHandler';

// Repository implementations
import { AccountWriteRepositoryImpl } from './infrastructure/persistence/repository/AccountWriteRepositoryImpl';
import { AccountReadRepositoryImpl } from './infrastructure/persistence/repository/AccountReadRepositoryImpl';

// Infrastructure modules
import { AuthModule } from './infrastructure/auth/AuthModule';
import { DatabaseModule } from './infrastructure/config/DatabaseModule';

// Injection tokens
import {
  ACCOUNT_WRITE_REPOSITORY,
  ACCOUNT_READ_REPOSITORY,
} from './infrastructure/config/InjectionToken';

@Module({
  imports: [CqrsModule, AuthModule, DatabaseModule],
  controllers: [AccountController],
  providers: [
    // Command handlers
    CreateAccountHandler,
    UpdateAccountHandler,
    DeleteAccountHandler,

    // Query handlers
    GetAccountsHandler,
    GetAccountByIdHandler,

    // Repository implementations
    { provide: ACCOUNT_WRITE_REPOSITORY, useClass: AccountWriteRepositoryImpl },
    { provide: ACCOUNT_READ_REPOSITORY, useClass: AccountReadRepositoryImpl },
  ],
  exports: [AuthModule],
})
export class AccountModule {}
