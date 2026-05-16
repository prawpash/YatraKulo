import { Test, TestingModule } from '@nestjs/testing';
import { GetAccountsHandler } from '@app/account/application/query/GetAccountsHandler';
import { GetAccountsQuery } from '@app/account/application/query/GetAccountsQuery';
import { ACCOUNT_READ_REPOSITORY } from '@app/account/infrastructure/config/InjectionToken';
import { createDomainPageRequest, createDomainPage } from '@yk/shared';
import { Account } from '@app/account/domain/entity/Account';
import { AccountReadRepository } from '@app/account/domain/repository/AccountReadRepository';

describe('GetAccountsHandler', () => {
  let handler: GetAccountsHandler;
  let readRepository: jest.Mocked<AccountReadRepository>;
  let getAccountsMock: jest.Mock;

  beforeEach(async () => {
    getAccountsMock = jest.fn();
    readRepository = {
      getAccounts: getAccountsMock,
      findById: jest.fn(),
      findByIdAndWorkspaceId: jest.fn(),
    } as unknown as jest.Mocked<AccountReadRepository>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        GetAccountsHandler,
        {
          provide: ACCOUNT_READ_REPOSITORY,
          useValue: readRepository,
        },
      ],
    }).compile();

    handler = module.get<GetAccountsHandler>(GetAccountsHandler);
  });

  it('should return a page of accounts', async () => {
    const pageRequest = createDomainPageRequest(1, 10);
    const query = new GetAccountsQuery(false, pageRequest, 'ws-1');

    const expectedPage = createDomainPage<Account>([], 0, 0, 1, 10);
    getAccountsMock.mockResolvedValue(expectedPage);

    const result = await handler.execute(query);

    expect(result).toBe(expectedPage);
    expect(getAccountsMock).toHaveBeenCalledWith({
      workspaceId: 'ws-1',
      includeGlobal: false,
      searchTerm: undefined,
      parentId: undefined,
      pageRequest,
    });
  });
});
