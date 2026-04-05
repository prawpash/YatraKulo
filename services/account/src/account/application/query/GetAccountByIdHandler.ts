import { Inject } from '@nestjs/common';
import { IQueryHandler, QueryHandler } from '@nestjs/cqrs';
import { GetAccountByIdQuery } from './GetAccountByIdQuery';
import type { AccountReadRepository } from '@app/account/domain/repository/AccountReadRepository';
import { ACCOUNT_READ_REPOSITORY } from '@app/account/infrastructure/config/InjectionToken';
import { Account } from '@app/account/domain/entity/Account';
import { NotFoundException } from '@yk/shared';

@QueryHandler(GetAccountByIdQuery)
export class GetAccountByIdHandler implements IQueryHandler<GetAccountByIdQuery> {
  constructor(
    @Inject(ACCOUNT_READ_REPOSITORY)
    private readonly accountReadRepository: AccountReadRepository,
  ) {}

  async execute(query: GetAccountByIdQuery): Promise<Account> {
    const { id } = query;
    const account = await this.accountReadRepository.findById(id);

    if (!account) {
      throw new NotFoundException(`Account with ID ${id} not found`);
    }

    return account;
  }
}
