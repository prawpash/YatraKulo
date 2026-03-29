import { Account } from '@app/account/domain/entity/Account';
import { DomainPage, DomainPageRequest } from '@yk/shared';

export interface AccountReadRepository {
  findById(id: string): Promise<Account | null>;

  findByWorkspaceId(
    workspaceId: string,
    pageRequest: DomainPageRequest,
    search?: string,
  ): Promise<DomainPage<Account>>;

  findByParentId(
    parentId: string | null,
    pageRequest: DomainPageRequest,
  ): Promise<DomainPage<Account>>;

  findAll(pageRequest: DomainPageRequest): Promise<DomainPage<Account>>;
}
