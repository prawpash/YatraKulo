import { Account } from '@app/account/domain/entity/Account';

export interface AccountWriteRepository {
  save(account: Account): Promise<Account>;

  delete(workspaceId: string, id: string): Promise<void>;
}
