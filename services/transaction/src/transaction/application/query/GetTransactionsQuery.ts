import { DomainPageRequest } from '@yk/shared';

export class GetTransactionsQuery {
  constructor(
    public readonly workspaceId: string,
    public readonly pageRequest: DomainPageRequest,
    public readonly fromAccountId?: string,
    public readonly toAccountId?: string,
  ) {}
}
