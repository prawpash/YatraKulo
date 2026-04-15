import { Account, AccountBuilder } from '@app/account/domain/entity/Account';
import { AccountType } from '@app/account/domain/enums/AccountType';
import { Selectable } from 'kysely';
import { AccountPersistence } from '@app/account/infrastructure/config/db';

export class AccountMapper {
  static toDomain(row: Selectable<AccountPersistence>): Account {
    if (!Object.values(AccountType).includes(row.type as AccountType)) {
      throw new Error(`Invalid account type: ${row.type}`);
    }

    return new AccountBuilder()
      .withId(row.id)
      .withName(row.name)
      .withDescription(row.description)
      .withType(row.type as AccountType)
      .withWorkspaceId(row.workspace_id)
      .withParentId(row.parent_id)
      .withCreatedAt(row.created_at as Date)
      .withUpdatedAt(row.updated_at as Date)
      .withDeletedAt(row.deleted_at as Date)
      .withCreatedBy(row.created_by)
      .withUpdatedBy(row.updated_by)
      .withDeletedBy(row.deleted_by)
      .build();
  }
  static toPersistence(account: Account) {
    return {
      id: account.id,
      name: account.name,
      description: account.description,
      type: account.type,
      workspace_id: account.workspaceId,
      parent_id: account.parentId,
      created_at: account.createdAt,
      updated_at: account.updatedAt,
      deleted_at: account.deletedAt,
      created_by: account.createdBy,
      updated_by: account.updatedBy,
      deleted_by: account.deletedBy,
    };
  }
}
