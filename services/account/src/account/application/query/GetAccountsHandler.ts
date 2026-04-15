import { Inject } from '@nestjs/common';
import { IQueryHandler, QueryHandler } from '@nestjs/cqrs';
import { GetAccountsQuery } from './GetAccountsQuery';
import type { AccountReadRepository } from '@app/account/domain/repository/AccountReadRepository';
import { ACCOUNT_READ_REPOSITORY } from '@app/account/infrastructure/config/InjectionToken';
import { Account } from '@app/account/domain/entity/Account';
import { DomainPage } from '@yk/shared';

@QueryHandler(GetAccountsQuery)
export class GetAccountsHandler implements IQueryHandler<GetAccountsQuery> {
  constructor(
    @Inject(ACCOUNT_READ_REPOSITORY)
    private readonly accountReadRepository: AccountReadRepository,
  ) {}

  async execute(query: GetAccountsQuery): Promise<DomainPage<Account>> {
    const { workspaceId, includeGlobal, searchTerm, parentId, pageRequest } =
      query;

    return this.accountReadRepository.getAccounts({
      workspaceId,
      includeGlobal,
      searchTerm,
      parentId,
      pageRequest,
    });
  }
}
