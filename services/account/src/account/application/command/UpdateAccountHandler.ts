import { CommandHandler, ICommandHandler } from '@nestjs/cqrs';
import { UpdateAccountCommand } from './UpdateAccountCommand';
import type { AccountWriteRepository } from '@app/account/domain/repository/AccountWriteRepository';
import type { AccountReadRepository } from '@app/account/domain/repository/AccountReadRepository';
import { NotFoundException } from '@yk/shared';

@CommandHandler(UpdateAccountCommand)
export class UpdateAccountHandler implements ICommandHandler<UpdateAccountCommand> {
  constructor(
    private readonly accountWriteRepository: AccountWriteRepository,
    private readonly accountReadRepository: AccountReadRepository,
  ) { }

  async execute(command: UpdateAccountCommand): Promise<void> {
    const { id, name, description, parentId, type, updatedBy } = command;

    const account = await this.accountReadRepository.findById(id);
    if (!account) {
      throw new NotFoundException(`Account with ID ${id} not found`);
    }

    const updatedAt = new Date();

    if (name !== undefined) {
      account.rename(name, updatedAt, updatedBy);
    }

    if (description !== undefined) {
      account.changeDescription(description, updatedAt, updatedBy);
    }

    if (type !== undefined) {
      account.changeType(type, updatedAt, updatedBy);
    }

    if (parentId !== undefined) {
      if (parentId !== null) {
        const parentAccount = await this.accountReadRepository.findById(parentId);
        if (!parentAccount) {
          throw new NotFoundException(`Parent account with ID ${parentId} not found`);
        }
      }
      account.changeParent(parentId, updatedAt, updatedBy);
    }

    await this.accountWriteRepository.save(account);
  }
}
