import { CommandHandler, ICommandHandler } from '@nestjs/cqrs';
import { Inject } from '@nestjs/common';
import { CreateAccountCommand } from './CreateAccountCommand';
import type { AccountWriteRepository } from '@app/account/domain/repository/AccountWriteRepository';
import type { AccountReadRepository } from '@app/account/domain/repository/AccountReadRepository';
import {
  ACCOUNT_WRITE_REPOSITORY,
  ACCOUNT_READ_REPOSITORY,
} from '@app/account/infrastructure/config/InjectionToken';
import { AccountBuilder } from '@app/account/domain/entity/Account';
import { v4 as uuidv4 } from 'uuid';
import { NotFoundException } from '@yk/shared';

import { Account } from '@app/account/domain/entity/Account';

@CommandHandler(CreateAccountCommand)
export class CreateAccountHandler implements ICommandHandler<CreateAccountCommand> {
  constructor(
    @Inject(ACCOUNT_WRITE_REPOSITORY)
    private readonly accountWriteRepository: AccountWriteRepository,
    @Inject(ACCOUNT_READ_REPOSITORY)
    private readonly accountReadRepository: AccountReadRepository,
  ) {}

  async execute(command: CreateAccountCommand): Promise<Account> {
    const { name, type, workspaceId, parentId, description, createdBy } =
      command;

    if (parentId) {
      const parentAccount = await this.accountReadRepository.findById(parentId);

      if (!parentAccount) {
        throw new NotFoundException(
          `Parent account with ID ${parentId} not found`,
        );
      }
    }

    const account = new AccountBuilder()
      .withId(uuidv4())
      .withName(name)
      .withType(type)
      .withWorkspaceId(workspaceId)
      .withParentId(parentId)
      .withDescription(description)
      .withCreatedBy(createdBy)
      .withCreatedAt(new Date())
      .withUpdatedAt(new Date())
      .build();

    await this.accountWriteRepository.save(account);

    return account;
  }
}
