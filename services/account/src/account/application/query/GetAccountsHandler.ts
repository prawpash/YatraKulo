import { IQueryHandler, QueryHandler } from '@nestjs/cqrs';
import { GetAccountsQuery } from './GetAccountsQuery';
import type { AccountReadRepository } from '@app/account/domain/repository/AccountReadRepository';
import { Account } from '@app/account/domain/entity/Account';
import { DomainPage } from '@yk/shared';

@QueryHandler(GetAccountsQuery)
export class GetAccountsHandler implements IQueryHandler<GetAccountsQuery> {
  constructor(private readonly accountReadRepository: AccountReadRepository) {}

  async execute(query: GetAccountsQuery): Promise<DomainPage<Account>> {
    const { workspaceId, includeGlobal, searchTerm, parentId, pageRequest } = query;

    return this.accountReadRepository.getAccounts({
      workspaceId,
      includeGlobal,
      searchTerm,
      parentId,
      pageRequest,
    });
  }
}
