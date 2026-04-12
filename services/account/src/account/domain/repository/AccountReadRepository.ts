import { Account } from '@app/account/domain/entity/Account';
import { DomainPage, DomainPageRequest } from '@yk/shared';

export interface GetAccountsParams {
  workspaceId?: string;
  includeGlobal: boolean;
  searchTerm?: string;
  parentId?: string | null;
  pageRequest: DomainPageRequest;
}

export interface AccountReadRepository {
  findById(id: string): Promise<Account | null>;

  findByIdAndWorkspaceId(
    id: string,
    workspaceId: string,
  ): Promise<Account | null>;

  getAccounts(params: GetAccountsParams): Promise<DomainPage<Account>>;
}
