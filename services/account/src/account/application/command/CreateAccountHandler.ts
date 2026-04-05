import { CommandHandler, ICommandHandler } from '@nestjs/cqrs';
import { CreateAccountCommand } from './CreateAccountCommand';
import type { AccountWriteRepository } from '@app/account/domain/repository/AccountWriteRepository';
import type { AccountReadRepository } from '@app/account/domain/repository/AccountReadRepository';
import { AccountBuilder } from '@app/account/domain/entity/Account';
import { v4 as uuidv4 } from 'uuid';
import { NotFoundException } from '@yk/shared';

import { Account } from '@app/account/domain/entity/Account';

@CommandHandler(CreateAccountCommand)
export class CreateAccountHandler implements ICommandHandler<CreateAccountCommand> {

  constructor(
    private readonly accountWriteRepository: AccountWriteRepository,
    private readonly accountReadRepository: AccountReadRepository,
  ) { }

  async execute(command: CreateAccountCommand): Promise<Account> {
    const { name, type, workspaceId, parentId, description, createdBy } = command;

    if (parentId) {
      const parentAccount = await this.accountReadRepository.findById(parentId);

      if (!parentAccount) {
        throw new NotFoundException(`Parent account with ID ${parentId} not found`);
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
