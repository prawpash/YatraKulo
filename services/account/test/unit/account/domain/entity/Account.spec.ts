import { AccountBuilder } from '@app/account/domain/entity/Account';
import { AccountType } from '@app/account/domain/enums/AccountType';
import { DomainRuleViolationException } from '@yk/shared';

describe('Account', () => {
  const validId = 'acc-123';
  const validName = 'Test Account';
  const validType = AccountType.ASSET;

  it('should create a valid account', () => {
    const account = new AccountBuilder()
      .withId(validId)
      .withName(validName)
      .withType(validType)
      .build();

    expect(account.id).toBe(validId);
    expect(account.name).toBe(validName);
    expect(account.type).toBe(validType);
  });

  it('should throw if id is missing', () => {
    const builder = new AccountBuilder()
      .withName(validName)
      .withType(validType);
    expect(() => builder.build()).toThrow(DomainRuleViolationException);
    expect(() => builder.build()).toThrow('id is required');
  });

  it('should throw if name is blank', () => {
    const builder = new AccountBuilder()
      .withId(validId)
      .withName(' ')
      .withType(validType);
    expect(() => builder.build()).toThrow(DomainRuleViolationException);
    expect(() => builder.build()).toThrow('name must not be blank');
  });

  it('should throw if name is too long', () => {
    const longName = 'a'.repeat(101);
    const builder = new AccountBuilder()
      .withId(validId)
      .withName(longName)
      .withType(validType);
    expect(() => builder.build()).toThrow(DomainRuleViolationException);
    expect(() => builder.build()).toThrow(
      'name must not exceed 100 characters',
    );
  });

  it('should throw if parentId is same as id', () => {
    const builder = new AccountBuilder()
      .withId(validId)
      .withName(validName)
      .withType(validType)
      .withParentId(validId);
    expect(() => builder.build()).toThrow(DomainRuleViolationException);
    expect(() => builder.build()).toThrow('parentId cannot be same as id');
  });

  it('should rename account', () => {
    const account = new AccountBuilder()
      .withId(validId)
      .withName(validName)
      .withType(validType)
      .build();

    const newName = 'New Name';
    const updatedAt = new Date();
    account.rename(newName, updatedAt, 'user-1');

    expect(account.name).toBe(newName);
    expect(account.updatedAt).toBe(updatedAt);
    expect(account.updatedBy).toBe('user-1');
  });

  it('should throw when renaming deleted account', () => {
    const account = new AccountBuilder()
      .withId(validId)
      .withName(validName)
      .withType(validType)
      .build();

    account.delete(new Date(), 'user-1');

    expect(() => account.rename('New Name', new Date(), 'user-1')).toThrow(
      DomainRuleViolationException,
    );
    expect(() => account.rename('New Name', new Date(), 'user-1')).toThrow(
      'Account is already deleted',
    );
  });

  it('should change description', () => {
    const account = new AccountBuilder()
      .withId(validId)
      .withName(validName)
      .withType(validType)
      .build();

    const newDesc = 'New Description';
    account.changeDescription(newDesc, new Date(), 'user-1');
    expect(account.description).toBe(newDesc);
  });

  it('should delete account', () => {
    const account = new AccountBuilder()
      .withId(validId)
      .withName(validName)
      .withType(validType)
      .build();

    const deletedAt = new Date();
    account.delete(deletedAt, 'user-1');

    expect(account.deletedAt).toBe(deletedAt);
    expect(account.deletedBy).toBe('user-1');
  });

  it('should throw when deleting already deleted account', () => {
    const account = new AccountBuilder()
      .withId(validId)
      .withName(validName)
      .withType(validType)
      .build();

    account.delete(new Date(), 'user-1');
    expect(() => account.delete(new Date(), 'user-1')).toThrow(
      DomainRuleViolationException,
    );
  });

  it('should change parent', () => {
    const account = new AccountBuilder()
      .withId(validId)
      .withName(validName)
      .withType(validType)
      .build();

    const newParentId = 'parent-456';
    account.changeParent(newParentId, new Date(), 'user-1');
    expect(account.parentId).toBe(newParentId);

    account.changeParent(null, new Date(), 'user-1');
    expect(account.parentId).toBeNull();
  });

  it('should throw if parentId is same as id in changeParent', () => {
    const account = new AccountBuilder()
      .withId(validId)
      .withName(validName)
      .withType(validType)
      .build();

    expect(() => account.changeParent(validId, new Date(), 'user-1')).toThrow(
      DomainRuleViolationException,
    );
  });

  it('should change type', () => {
    const account = new AccountBuilder()
      .withId(validId)
      .withName(validName)
      .withType(validType)
      .build();

    const newType = AccountType.LIABILITY;
    account.changeType(newType, new Date(), 'user-1');
    expect(account.type).toBe(newType);
  });

  it('should throw if type is missing in changeType', () => {
    const account = new AccountBuilder()
      .withId(validId)
      .withName(validName)
      .withType(validType)
      .build();

    expect(() =>
      account.changeType(undefined as any, new Date(), 'user-1'),
    ).toThrow(DomainRuleViolationException);
  });

  it('should return plain object', () => {
    const account = new AccountBuilder()
      .withId(validId)
      .withName(validName)
      .withType(validType)
      .withDescription('Desc')
      .withWorkspaceId('ws-1')
      .build();

    const plain = account.toPlainObject();
    expect(plain.id).toBe(validId);
    expect(plain.name).toBe(validName);
    expect(plain.description).toBe('Desc');
    expect(plain.workspaceId).toBe('ws-1');
  });

  it('should throw if deletedBy is missing in delete', () => {
    const account = new AccountBuilder()
      .withId(validId)
      .withName(validName)
      .withType(validType)
      .build();

    expect(() => account.delete(new Date(), undefined as any)).toThrow(
      DomainRuleViolationException,
    );
  });
});
