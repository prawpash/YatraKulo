import { Test, TestingModule } from '@nestjs/testing';
import { CreateAccountHandler } from '@app/account/application/command/CreateAccountHandler';
import { CreateAccountCommand } from '@app/account/application/command/CreateAccountCommand';
import { AccountType } from '@app/account/domain/enums/AccountType';
import {
  ACCOUNT_WRITE_REPOSITORY,
  ACCOUNT_READ_REPOSITORY,
} from '@app/shared/config/InjectionToken';
import { NotFoundException } from '@yk/shared';
import { AccountBuilder } from '@app/account/domain/entity/Account';
import { AccountWriteRepository } from '@app/account/domain/repository/AccountWriteRepository';
import { AccountReadRepository } from '@app/account/domain/repository/AccountReadRepository';

describe('CreateAccountHandler', () => {
  let handler: CreateAccountHandler;
  let writeRepository: jest.Mocked<AccountWriteRepository>;
  let readRepository: jest.Mocked<AccountReadRepository>;
  let saveMock: jest.Mock;
  let findByIdMock: jest.Mock;

  beforeEach(async () => {
    saveMock = jest.fn().mockResolvedValue(undefined);
    findByIdMock = jest.fn();

    writeRepository = {
      save: saveMock,
      delete: jest.fn(),
    } as unknown as jest.Mocked<AccountWriteRepository>;
    readRepository = {
      findById: findByIdMock,
      findByIdAndWorkspaceId: jest.fn(),
      getAccounts: jest.fn(),
    } as unknown as jest.Mocked<AccountReadRepository>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        CreateAccountHandler,
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

    handler = module.get<CreateAccountHandler>(CreateAccountHandler);
  });

  it('should create an account successfully', async () => {
    const command = new CreateAccountCommand(
      'Test Account',
      AccountType.ASSET,
      'ws-1',
      null,
      'Desc',
      'user-1',
    );

    const result = await handler.execute(command);

    expect(result).toBeDefined();
    expect(result.name).toBe(command.name);
    expect(saveMock).toHaveBeenCalled();
  });

  it('should create an account with parent successfully', async () => {
    const parentId = 'parent-123';
    const parentAccount = new AccountBuilder()
      .withId(parentId)
      .withName('Parent')
      .withType(AccountType.ASSET)
      .build();
    findByIdMock.mockResolvedValue(parentAccount);

    const command = new CreateAccountCommand(
      'Child Account',
      AccountType.ASSET,
      'ws-1',
      parentId,
      null,
      'user-1',
    );

    const result = await handler.execute(command);

    expect(result.parentId).toBe(parentId);
    expect(findByIdMock).toHaveBeenCalledWith(parentId);
    expect(saveMock).toHaveBeenCalled();
  });

  it('should throw NotFoundException if parent does not exist', async () => {
    findByIdMock.mockResolvedValue(null);

    const command = new CreateAccountCommand(
      'Child Account',
      AccountType.ASSET,
      'ws-1',
      'non-existent',
      null,
      'user-1',
    );

    await expect(handler.execute(command)).rejects.toThrow(NotFoundException);
  });
});
