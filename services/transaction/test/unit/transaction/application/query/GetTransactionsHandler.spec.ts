import { Test, TestingModule } from '@nestjs/testing';
import { GetTransactionsHandler } from '@app/transaction/application/query/GetTransactionsHandler';
import { GetTransactionsQuery } from '@app/transaction/application/query/GetTransactionsQuery';
import { TRANSACTION_READ_REPOSITORY } from '@app/transaction/infrastructure/config/InjectionToken';
import { TransactionReadRepository } from '@app/transaction/domain/repository/TransactionReadRepository';
import { createDomainPage, createDomainPageRequest } from '@yk/shared';

describe('GetTransactionsHandler', () => {
  let handler: GetTransactionsHandler;
  let readRepository: jest.Mocked<TransactionReadRepository>;
  let getTransactionsMock: jest.Mock;

  beforeEach(async () => {
    getTransactionsMock = jest.fn();

    readRepository = {
      findById: jest.fn(),
      findByIdAndWorkspaceId: jest.fn(),
      findByIdempotencyKeyAndWorkspaceId: jest.fn(),
      getTransactions: getTransactionsMock,
    } as unknown as jest.Mocked<TransactionReadRepository>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        GetTransactionsHandler,
        {
          provide: TRANSACTION_READ_REPOSITORY,
          useValue: readRepository,
        },
      ],
    }).compile();

    handler = module.get<GetTransactionsHandler>(GetTransactionsHandler);
  });

  it('should return paginated transactions from repository', async () => {
    const pageRequest = createDomainPageRequest(0, 10);
    const domainPage = createDomainPage([], 0, 0, 0, 10);
    getTransactionsMock.mockResolvedValue(domainPage);

    const query = new GetTransactionsQuery(
      'ws-123',
      pageRequest,
      'acc-from',
      'acc-to',
    );

    const result = await handler.execute(query);

    expect(result).toBe(domainPage);
    expect(getTransactionsMock).toHaveBeenCalledWith({
      workspaceId: 'ws-123',
      fromAccountId: 'acc-from',
      toAccountId: 'acc-to',
      pageRequest,
    });
  });
});
