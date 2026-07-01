import { Test, TestingModule } from '@nestjs/testing';
import { GetTransactionByIdHandler } from '@app/transaction/application/query/GetTransactionByIdHandler';
import { GetTransactionByIdQuery } from '@app/transaction/application/query/GetTransactionByIdQuery';
import { TRANSACTION_READ_REPOSITORY } from '@app/shared/config/InjectionToken';
import { TransactionReadRepository } from '@app/transaction/domain/repository/TransactionReadRepository';
import { TransactionBuilder } from '@app/transaction/domain/entity/Transaction';
import { NotFoundException } from '@yk/shared';

describe('GetTransactionByIdHandler', () => {
  let handler: GetTransactionByIdHandler;
  let readRepository: jest.Mocked<TransactionReadRepository>;
  let findByIdAndWorkspaceIdMock: jest.Mock;

  beforeEach(async () => {
    findByIdAndWorkspaceIdMock = jest.fn();

    readRepository = {
      findById: jest.fn(),
      findByIdAndWorkspaceId: findByIdAndWorkspaceIdMock,
      findByIdempotencyKeyAndWorkspaceId: jest.fn(),
      getTransactions: jest.fn(),
    } as unknown as jest.Mocked<TransactionReadRepository>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        GetTransactionByIdHandler,
        {
          provide: TRANSACTION_READ_REPOSITORY,
          useValue: readRepository,
        },
      ],
    }).compile();

    handler = module.get<GetTransactionByIdHandler>(GetTransactionByIdHandler);
  });

  it('should return the transaction if it exists', async () => {
    const transaction = new TransactionBuilder()
      .withId('tx-123')
      .withAmount(100)
      .withFromAccountId('acc-from')
      .withToAccountId('acc-to')
      .withIdempotencyKey('idem-key')
      .withWorkspaceId('ws-123')
      .withCreatedBy('user-1')
      .withUpdatedBy('user-1')
      .reconstitute();

    findByIdAndWorkspaceIdMock.mockResolvedValue(transaction);

    const query = new GetTransactionByIdQuery('ws-123', 'tx-123');

    const result = await handler.execute(query);

    expect(result).toBe(transaction);
    expect(findByIdAndWorkspaceIdMock).toHaveBeenCalledWith('tx-123', 'ws-123');
  });

  it('should throw NotFoundException if transaction does not exist', async () => {
    findByIdAndWorkspaceIdMock.mockResolvedValue(null);

    const query = new GetTransactionByIdQuery('ws-123', 'tx-123');

    await expect(handler.execute(query)).rejects.toThrow(NotFoundException);
  });
});
