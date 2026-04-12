import { Inject, Injectable } from '@nestjs/common';
import { Kysely } from 'kysely';
import { Account } from '@app/account/domain/entity/Account';
import { AccountWriteRepository } from '@app/account/domain/repository/AccountWriteRepository';
import { DATABASE_CONNECTION } from '@app/account/infrastructure/config/InjectionToken';
import { DB } from '@app/account/infrastructure/config/db';
import { AccountMapper } from '@app/account/infrastructure/persistence/mapper/AccountMapper';

@Injectable()
export class AccountWriteRepositoryImpl implements AccountWriteRepository {
  constructor(
    @Inject(DATABASE_CONNECTION)
    private readonly db: Kysely<DB>,
  ) {}

  async save(account: Account): Promise<Account> {
    const persistence = AccountMapper.toPersistence(account);

    await this.db
      .insertInto('account')
      .values(persistence)
      .onConflict((oc) =>
        oc.column('id').doUpdateSet({
          name: persistence.name,
          description: persistence.description,
          type: persistence.type,
          workspace_id: persistence.workspace_id,
          parent_id: persistence.parent_id,
          updated_at: persistence.updated_at,
          updated_by: persistence.updated_by,
          deleted_at: persistence.deleted_at,
          deleted_by: persistence.deleted_by,
        }),
      )
      .execute();

    return account;
  }

  async delete(workspaceId: string, id: string): Promise<void> {
    await this.db
      .deleteFrom('account')
      .where('workspace_id', '=', workspaceId)
      .where('id', '=', id)
      .execute();
  }
}
