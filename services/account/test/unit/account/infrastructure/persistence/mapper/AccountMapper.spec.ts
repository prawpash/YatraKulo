import { AccountMapper } from '@app/account/infrastructure/persistence/mapper/AccountMapper';
import { AccountBuilder } from '@app/account/domain/entity/Account';
import { AccountType } from '@app/account/domain/enums/AccountType';
import { DomainRuleViolationException } from '@yk/shared';

describe('AccountMapper', () => {
  const row = {
    id: 'acc-1',
    name: 'Test',
    description: 'Desc',
    type: 'ASSET',
    workspace_id: 'ws-1',
    parent_id: 'parent-1',
    created_at: new Date(),
    updated_at: new Date(),
    deleted_at: null,
    created_by: 'user-1',
    updated_by: 'user-1',
    deleted_by: null,
  };

  it('should map from persistence to domain', () => {
    const domain = AccountMapper.toDomain(
      row as unknown as Parameters<typeof AccountMapper.toDomain>[0],
    );

    expect(domain.id).toBe(row.id);
    expect(domain.name).toBe(row.name);
    expect(domain.type).toBe(AccountType.ASSET);
    expect(domain.workspaceId).toBe(row.workspace_id);
  });

  it('should throw error if invalid type in persistence', () => {
    const invalidRow = { ...row, type: 'INVALID' };
    expect(() =>
      AccountMapper.toDomain(
        invalidRow as unknown as Parameters<typeof AccountMapper.toDomain>[0],
      ),
    ).toThrow(DomainRuleViolationException);
  });

  it('should map from domain to persistence', () => {
    const account = new AccountBuilder()
      .withId('acc-1')
      .withName('Test')
      .withType(AccountType.ASSET)
      .withWorkspaceId('ws-1')
      .build();

    const persistence = AccountMapper.toPersistence(account);

    expect(persistence.id).toBe(account.id);
    expect(persistence.name).toBe(account.name);
    expect(persistence.type).toBe(account.type);
    expect(persistence.workspace_id).toBe(account.workspaceId);
  });
});
