import { CommandHandler, ICommandHandler } from '@nestjs/cqrs';
import { DeleteAccountCommand } from './DeleteAccountCommand';
import type { AccountWriteRepository } from '@app/account/domain/repository/AccountWriteRepository';
import type { AccountReadRepository } from '@app/account/domain/repository/AccountReadRepository';
import { NotFoundException } from '@yk/shared';

@CommandHandler(DeleteAccountCommand)
export class DeleteAccountHandler implements ICommandHandler<DeleteAccountCommand> {

  constructor(
    private readonly accountWriteRepository: AccountWriteRepository,
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
