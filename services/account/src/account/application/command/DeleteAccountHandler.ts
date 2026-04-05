import { CommandHandler, ICommandHandler } from '@nestjs/cqrs';
import { Inject } from '@nestjs/common';
import { DeleteAccountCommand } from './DeleteAccountCommand';
import type { AccountWriteRepository } from '@app/account/domain/repository/AccountWriteRepository';
import type { AccountReadRepository } from '@app/account/domain/repository/AccountReadRepository';
import { ACCOUNT_WRITE_REPOSITORY, ACCOUNT_READ_REPOSITORY } from '@app/account/infrastructure/config/InjectionToken';
import { NotFoundException } from '@yk/shared';

@CommandHandler(DeleteAccountCommand)
export class DeleteAccountHandler implements ICommandHandler<DeleteAccountCommand> {

  constructor(
    @Inject(ACCOUNT_WRITE_REPOSITORY)
    private readonly accountWriteRepository: AccountWriteRepository,
    @Inject(ACCOUNT_READ_REPOSITORY)
    private readonly accountReadRepository: AccountReadRepository,
  ) { }

  async execute(command: DeleteAccountCommand): Promise<void> {
    const { id, deletedBy } = command;

    const account = await this.accountReadRepository.findById(id);

    if (!account) {
      throw new NotFoundException(`Account with ID ${id} not found`);
    }

    const deletedAt = new Date();

    account.delete(deletedAt, deletedBy);

    await this.accountWriteRepository.save(account);
  }
}
