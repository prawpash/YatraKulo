import { DomainPageRequest } from '@yk/shared';

export class GetAccountsQuery {
  constructor(
    public readonly includeGlobal: boolean,
    public readonly pageRequest: DomainPageRequest,
    public readonly workspaceId?: string,
    public readonly searchTerm?: string,
    public readonly parentId?: string | null,
  ) {}
}
