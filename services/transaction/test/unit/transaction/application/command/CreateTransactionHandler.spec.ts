import { Test, TestingModule } from '@nestjs/testing';
import { CreateTransactionHandler } from '@app/transaction/application/command/CreateTransactionHandler';
import { CreateTransactionCommand } from '@app/transaction/application/command/CreateTransactionCommand';
import {
  TRANSACTION_READ_REPOSITORY,
  TRANSACTION_WRITE_REPOSITORY,
} from '@app/shared/config/InjectionToken';
import { TransactionReadRepository } from '@app/transaction/domain/repository/TransactionReadRepository';
import { TransactionWriteRepository } from '@app/transaction/domain/repository/TransactionWriteRepository';
import { TransactionBuilder } from '@app/transaction/domain/entity/Transaction';

describe('CreateTransactionHandler', () => {
  let handler: CreateTransactionHandler;
  let readRepository: jest.Mocked<TransactionReadRepository>;
  let writeRepository: jest.Mocked<TransactionWriteRepository>;
  let findByIdempotencyKeyMock: jest.Mock;
  let saveMock: jest.Mock;

  beforeEach(async () => {
    findByIdempotencyKeyMock = jest.fn();
    saveMock = jest.fn();

    readRepository = {
      findById: jest.fn(),
      findByIdAndWorkspaceId: jest.fn(),
      findByIdempotencyKeyAndWorkspaceId: findByIdempotencyKeyMock,
      getTransactions: jest.fn(),
    } as unknown as jest.Mocked<TransactionReadRepository>;

    writeRepository = {
      save: saveMock,
    } as unknown as jest.Mocked<TransactionWriteRepository>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        CreateTransactionHandler,
        {
          provide: TRANSACTION_READ_REPOSITORY,
          useValue: readRepository,
        },
        {
          provide: TRANSACTION_WRITE_REPOSITORY,
          useValue: writeRepository,
        },
      ],
    }).compile();

    handler = module.get<CreateTransactionHandler>(CreateTransactionHandler);
  });

  it('should return existing transaction if matching idempotency key is found', async () => {
    const existingTx = new TransactionBuilder()
      .withId('tx-123')
      .withAmount(100)
      .withFromAccountId('acc-from')
      .withToAccountId('acc-to')
      .withIdempotencyKey('idem-key')
      .withWorkspaceId('ws-123')
      .withCreatedBy('user-1')
      .withUpdatedBy('user-1')
      .reconstitute();

    findByIdempotencyKeyMock.mockResolvedValue(existingTx);

    const command = new CreateTransactionCommand(
      'ws-123',
      100,
      'Note',
      'acc-from',
      'acc-to',
      'idem-key',
      'user-1',
    );

    const result = await handler.execute(command);

    expect(result).toBe(existingTx);
    expect(findByIdempotencyKeyMock).toHaveBeenCalledWith('idem-key', 'ws-123');
    expect(saveMock).not.toHaveBeenCalled();
  });

  it('should create and save a new transaction if no existing one is found', async () => {
    findByIdempotencyKeyMock.mockResolvedValue(null);
    saveMock.mockImplementation((tx: unknown) => Promise.resolve(tx));

    const command = new CreateTransactionCommand(
      'ws-123',
      150.5,
      'New Tx',
      'acc-from',
      'acc-to',
      'new-idem-key',
      'user-1',
    );

    const result = await handler.execute(command);

    expect(result).toBeDefined();
    expect(result.amount).toBe(150.5);
    expect(result.note).toBe('New Tx');
    expect(result.fromAccountId).toBe('acc-from');
    expect(result.toAccountId).toBe('acc-to');
    expect(result.idempotencyKey).toBe('new-idem-key');
    expect(result.workspaceId).toBe('ws-123');
    expect(result.createdBy).toBe('user-1');
    expect(saveMock).toHaveBeenCalled();
  });
});
