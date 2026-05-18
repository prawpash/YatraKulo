import { Test, TestingModule } from '@nestjs/testing';
import { DeleteAccountHandler } from '@app/account/application/command/DeleteAccountHandler';
import { DeleteAccountCommand } from '@app/account/application/command/DeleteAccountCommand';
import { AccountType } from '@app/account/domain/enums/AccountType';
import {
  ACCOUNT_WRITE_REPOSITORY,
  ACCOUNT_READ_REPOSITORY,
} from '@app/account/infrastructure/config/InjectionToken';
import { NotFoundException } from '@yk/shared';
import { AccountBuilder } from '@app/account/domain/entity/Account';
import { AccountWriteRepository } from '@app/account/domain/repository/AccountWriteRepository';
import { AccountReadRepository } from '@app/account/domain/repository/AccountReadRepository';

describe('DeleteAccountHandler', () => {
  let handler: DeleteAccountHandler;
  let writeRepository: jest.Mocked<AccountWriteRepository>;
  let readRepository: jest.Mocked<AccountReadRepository>;
  let saveMock: jest.Mock;
  let findByIdAndWorkspaceIdMock: jest.Mock;

  beforeEach(async () => {
    saveMock = jest.fn().mockResolvedValue(undefined);
    findByIdAndWorkspaceIdMock = jest.fn();

    writeRepository = {
      save: saveMock,
      delete: jest.fn(),
    } as unknown as jest.Mocked<AccountWriteRepository>;
    readRepository = {
      findByIdAndWorkspaceId: findByIdAndWorkspaceIdMock,
      findById: jest.fn(),
      getAccounts: jest.fn(),
    } as unknown as jest.Mocked<AccountReadRepository>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        DeleteAccountHandler,
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

    handler = module.get<DeleteAccountHandler>(DeleteAccountHandler);
  });

  it('should delete an account successfully', async () => {
    const workspaceId = 'ws-1';
    const accountId = 'acc-1';
    const account = new AccountBuilder()
      .withId(accountId)
      .withName('Test Account')
      .withType(AccountType.ASSET)
      .withWorkspaceId(workspaceId)
      .build();
    findByIdAndWorkspaceIdMock.mockResolvedValue(account);

    const command = new DeleteAccountCommand(workspaceId, accountId, 'user-1');

    await handler.execute(command);

    expect(account.deletedAt).toBeDefined();
    expect(account.deletedBy).toBe('user-1');
    expect(saveMock).toHaveBeenCalledWith(account);
  });

  it('should throw NotFoundException if account does not exist', async () => {
    findByIdAndWorkspaceIdMock.mockResolvedValue(null);

    const command = new DeleteAccountCommand('ws-1', 'acc-1', 'user-1');

    await expect(handler.execute(command)).rejects.toThrow(NotFoundException);
  });
});
