import { Test, TestingModule } from '@nestjs/testing';
import { UpdateAccountHandler } from '@app/account/application/command/UpdateAccountHandler';
import { UpdateAccountCommand } from '@app/account/application/command/UpdateAccountCommand';
import { AccountType } from '@app/account/domain/enums/AccountType';
import {
  ACCOUNT_WRITE_REPOSITORY,
  ACCOUNT_READ_REPOSITORY,
} from '@app/account/infrastructure/config/InjectionToken';
import { NotFoundException } from '@yk/shared';
import { AccountBuilder } from '@app/account/domain/entity/Account';
import { AccountWriteRepository } from '@app/account/domain/repository/AccountWriteRepository';
import { AccountReadRepository } from '@app/account/domain/repository/AccountReadRepository';

describe('UpdateAccountHandler', () => {
  let handler: UpdateAccountHandler;
  let writeRepository: jest.Mocked<AccountWriteRepository>;
  let readRepository: jest.Mocked<AccountReadRepository>;
  let saveMock: jest.Mock;
  let findByIdAndWorkspaceIdMock: jest.Mock;
  let findByIdMock: jest.Mock;

  beforeEach(async () => {
    saveMock = jest.fn().mockResolvedValue(undefined);
    findByIdAndWorkspaceIdMock = jest.fn();
    findByIdMock = jest.fn();

    writeRepository = {
      save: saveMock,
      delete: jest.fn(),
    } as unknown as jest.Mocked<AccountWriteRepository>;
    readRepository = {
      findByIdAndWorkspaceId: findByIdAndWorkspaceIdMock,
      findById: findByIdMock,
      getAccounts: jest.fn(),
    } as unknown as jest.Mocked<AccountReadRepository>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        UpdateAccountHandler,
        {
          provide: ACCOUNT_WRITE_REPOSITORY,
          useValue: writeRepository,
        },
        {
          provide: ACCOUNT_READ_REPOSITORY,
          useValue: readRepository,
        },
      ],
    }).compile();

    handler = module.get<UpdateAccountHandler>(UpdateAccountHandler);
  });

  it('should update an account successfully', async () => {
    const workspaceId = 'ws-1';
    const accountId = 'acc-1';
    const account = new AccountBuilder()
      .withId(accountId)
      .withName('Old Name')
      .withType(AccountType.ASSET)
      .withWorkspaceId(workspaceId)
      .build();
    findByIdAndWorkspaceIdMock.mockResolvedValue(account);

    const command = new UpdateAccountCommand(
      workspaceId,
      accountId,
      'user-1',
      'New Name',
      'New Desc',
    );

    await handler.execute(command);

    expect(account.name).toBe('New Name');
    expect(account.description).toBe('New Desc');
    expect(saveMock).toHaveBeenCalledWith(account);
  });

  it('should throw NotFoundException if account does not exist', async () => {
    findByIdAndWorkspaceIdMock.mockResolvedValue(null);

    const command = new UpdateAccountCommand('ws-1', 'acc-1', 'user-1', 'Name');

    await expect(handler.execute(command)).rejects.toThrow(NotFoundException);
  });

  it('should throw NotFoundException if parent account does not exist', async () => {
    const workspaceId = 'ws-1';
    const accountId = 'acc-1';
    const account = new AccountBuilder()
      .withId(accountId)
      .withName('Name')
      .withType(AccountType.ASSET)
      .withWorkspaceId(workspaceId)
      .build();
    findByIdAndWorkspaceIdMock.mockResolvedValue(account);
    findByIdMock.mockResolvedValue(null);

    const command = new UpdateAccountCommand(
      workspaceId,
      accountId,
      'user-1',
      undefined,
      undefined,
      'parent-1',
    );

    await expect(handler.execute(command)).rejects.toThrow(NotFoundException);
  });

  it('should throw NotFoundException if parent account belongs to another workspace', async () => {
    const workspaceId = 'ws-1';
    const accountId = 'acc-1';
    const account = new AccountBuilder()
      .withId(accountId)
      .withName('Name')
      .withType(AccountType.ASSET)
      .withWorkspaceId(workspaceId)
      .build();
    findByIdAndWorkspaceIdMock.mockResolvedValue(account);

    const parentAccount = new AccountBuilder()
      .withId('parent-1')
      .withName('Parent')
      .withType(AccountType.ASSET)
      .withWorkspaceId('ws-2') // Different workspace
      .build();
    findByIdMock.mockResolvedValue(parentAccount);

    const command = new UpdateAccountCommand(
      workspaceId,
      accountId,
      'user-1',
      undefined,
      undefined,
      'parent-1',
    );

    await expect(handler.execute(command)).rejects.toThrow(NotFoundException);
  });

  it('should update account type and parent successfully', async () => {
    const workspaceId = 'ws-1';
    const accountId = 'acc-1';
    const account = new AccountBuilder()
      .withId(accountId)
      .withName('Name')
      .withType(AccountType.ASSET)
      .withWorkspaceId(workspaceId)
      .build();
    findByIdAndWorkspaceIdMock.mockResolvedValue(account);

    const parentId = 'parent-1';
    const parentAccount = new AccountBuilder()
      .withId(parentId)
      .withName('Parent')
      .withType(AccountType.ASSET)
      .withWorkspaceId(workspaceId)
      .build();
    findByIdMock.mockResolvedValue(parentAccount);

    const command = new UpdateAccountCommand(
      workspaceId,
      accountId,
      'user-1',
      undefined,
      undefined,
      parentId,
      AccountType.LIABILITY,
    );

    await handler.execute(command);

    expect(account.type).toBe(AccountType.LIABILITY);
    expect(account.parentId).toBe(parentId);
    expect(saveMock).toHaveBeenCalledWith(account);
  });

  it('should update account with global parent successfully', async () => {
    const workspaceId = 'ws-1';
    const accountId = 'acc-1';
    const account = new AccountBuilder()
      .withId(accountId)
      .withName('Name')
      .withType(AccountType.ASSET)
      .withWorkspaceId(workspaceId)
      .build();
    findByIdAndWorkspaceIdMock.mockResolvedValue(account);

    const parentId = 'global-parent-1';
    const parentAccount = new AccountBuilder()
      .withId(parentId)
      .withName('Global Parent')
      .withType(AccountType.ASSET)
      .withWorkspaceId(null) // Global
      .build();
    findByIdMock.mockResolvedValue(parentAccount);

    const command = new UpdateAccountCommand(
      workspaceId,
      accountId,
      'user-1',
      undefined,
      undefined,
      parentId,
    );

    await handler.execute(command);

    expect(account.parentId).toBe(parentId);
  });
});
