import { Test, TestingModule } from '@nestjs/testing';
import { GetAccountByIdHandler } from '@app/account/application/query/GetAccountByIdHandler';
import { GetAccountByIdQuery } from '@app/account/application/query/GetAccountByIdQuery';
import { AccountType } from '@app/account/domain/enums/AccountType';
import { ACCOUNT_READ_REPOSITORY } from '@app/shared/config/InjectionToken';
import { NotFoundException } from '@yk/shared';
import { AccountBuilder } from '@app/account/domain/entity/Account';
import { AccountReadRepository } from '@app/account/domain/repository/AccountReadRepository';

describe('GetAccountByIdHandler', () => {
  let handler: GetAccountByIdHandler;
  let readRepository: jest.Mocked<AccountReadRepository>;
  let findByIdAndWorkspaceIdMock: jest.Mock;

  beforeEach(async () => {
    findByIdAndWorkspaceIdMock = jest.fn();
    readRepository = {
      findByIdAndWorkspaceId: findByIdAndWorkspaceIdMock,
      findById: jest.fn(),
      getAccounts: jest.fn(),
    } as unknown as jest.Mocked<AccountReadRepository>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        GetAccountByIdHandler,
        {
          provide: ACCOUNT_READ_REPOSITORY,
          useValue: readRepository,
        },
      ],
    }).compile();

    handler = module.get<GetAccountByIdHandler>(GetAccountByIdHandler);
  });

  it('should return an account successfully', async () => {
    const workspaceId = 'ws-1';
    const accountId = 'acc-1';
    const account = new AccountBuilder()
      .withId(accountId)
      .withName('Test Account')
      .withType(AccountType.ASSET)
      .withWorkspaceId(workspaceId)
      .build();
    findByIdAndWorkspaceIdMock.mockResolvedValue(account);

    const query = new GetAccountByIdQuery(workspaceId, accountId);

    const result = await handler.execute(query);

    expect(result).toBe(account);
    expect(findByIdAndWorkspaceIdMock).toHaveBeenCalledWith(
      accountId,
      workspaceId,
    );
  });

  it('should throw NotFoundException if account does not exist', async () => {
    findByIdAndWorkspaceIdMock.mockResolvedValue(null);

    const query = new GetAccountByIdQuery('ws-1', 'acc-1');

    await expect(handler.execute(query)).rejects.toThrow(NotFoundException);
  });
});
