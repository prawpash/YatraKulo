import { Test, TestingModule } from '@nestjs/testing';
import { UpdateTransactionHandler } from '@app/transaction/application/command/UpdateTransactionHandler';
import { UpdateTransactionCommand } from '@app/transaction/application/command/UpdateTransactionCommand';
import {
  TRANSACTION_READ_REPOSITORY,
  TRANSACTION_WRITE_REPOSITORY,
} from '@app/transaction/infrastructure/config/InjectionToken';
import { TransactionReadRepository } from '@app/transaction/domain/repository/TransactionReadRepository';
import { TransactionWriteRepository } from '@app/transaction/domain/repository/TransactionWriteRepository';
import {
  TransactionBuilder,
  Transaction,
} from '@app/transaction/domain/entity/Transaction';
import { NotFoundException } from '@yk/shared';

describe('UpdateTransactionHandler', () => {
  let handler: UpdateTransactionHandler;
  let readRepository: jest.Mocked<TransactionReadRepository>;
  let writeRepository: jest.Mocked<TransactionWriteRepository>;
  let findByIdAndWorkspaceIdMock: jest.Mock;
  let saveMock: jest.Mock;

  beforeEach(async () => {
    findByIdAndWorkspaceIdMock = jest.fn();
    saveMock = jest.fn();

    readRepository = {
      findById: jest.fn(),
      findByIdAndWorkspaceId: findByIdAndWorkspaceIdMock,
      findByIdempotencyKeyAndWorkspaceId: jest.fn(),
      getTransactions: jest.fn(),
    } as unknown as jest.Mocked<TransactionReadRepository>;

    writeRepository = {
      save: saveMock,
    } as unknown as jest.Mocked<TransactionWriteRepository>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        UpdateTransactionHandler,
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

    handler = module.get<UpdateTransactionHandler>(UpdateTransactionHandler);
  });

  it('should throw NotFoundException if transaction does not exist', async () => {
    findByIdAndWorkspaceIdMock.mockResolvedValue(null);

    const command = new UpdateTransactionCommand(
      'ws-123',
      'tx-123',
      'user-1',
      200,
      'Updated',
    );

    await expect(handler.execute(command)).rejects.toThrow(NotFoundException);
    expect(findByIdAndWorkspaceIdMock).toHaveBeenCalledWith('tx-123', 'ws-123');
    expect(saveMock).not.toHaveBeenCalled();
  });

  it('should throw NotFoundException if user is not the creator of transaction', async () => {
    const existingTx = new TransactionBuilder()
      .withId('tx-123')
      .withAmount(100)
      .withFromAccountId('acc-from')
      .withToAccountId('acc-to')
      .withIdempotencyKey('idem-key')
      .withWorkspaceId('ws-123')
      .withCreatedBy('creator-user')
      .withUpdatedBy('creator-user')
      .reconstitute();

    findByIdAndWorkspaceIdMock.mockResolvedValue(existingTx);

    const command = new UpdateTransactionCommand(
      'ws-123',
      'tx-123',
      'unauthorized-user',
      200,
      'Updated',
    );

    await expect(handler.execute(command)).rejects.toThrow(NotFoundException);
    expect(saveMock).not.toHaveBeenCalled();
  });

  it('should successfully update and save transaction if user is creator', async () => {
    const existingTx = new TransactionBuilder()
      .withId('tx-123')
      .withAmount(100)
      .withFromAccountId('acc-from')
      .withToAccountId('acc-to')
      .withIdempotencyKey('idem-key')
      .withWorkspaceId('ws-123')
      .withCreatedBy('creator-user')
      .withUpdatedBy('creator-user')
      .reconstitute();

    findByIdAndWorkspaceIdMock.mockResolvedValue(existingTx);
    saveMock.mockImplementation((tx: unknown) => Promise.resolve(tx));

    const command = new UpdateTransactionCommand(
      'ws-123',
      'tx-123',
      'creator-user',
      200,
      'Updated Note',
    );

    await handler.execute(command);

    expect(saveMock).toHaveBeenCalled();
    const savedTx = (saveMock.mock.calls as unknown[][])[0][0] as Transaction;
    expect(savedTx).toBeDefined();
    expect(savedTx.amount).toBe(200);
    expect(savedTx.note).toBe('Updated Note');
    expect(savedTx.updatedBy).toBe('creator-user');
  });
});
