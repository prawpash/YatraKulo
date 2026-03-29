import { Account } from '@app/account/domain/entity/Account';

export interface AccountWriteRepository {
  save(account: Account): Promise<Account>;

  delete(id: string): Promise<void>;
}
