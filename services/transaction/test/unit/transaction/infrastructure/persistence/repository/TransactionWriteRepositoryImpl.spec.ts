import { Test, TestingModule } from '@nestjs/testing';
import { TransactionWriteRepositoryImpl } from '@app/transaction/infrastructure/persistence/repository/TransactionWriteRepositoryImpl';
import { DATABASE_CONNECTION } from '@app/transaction/infrastructure/config/InjectionToken';
import { TransactionBuilder } from '@app/transaction/domain/entity/Transaction';
import { Kysely } from 'kysely';
import { DB } from '@app/transaction/infrastructure/config/db';

describe('TransactionWriteRepositoryImpl', () => {
  let repository: TransactionWriteRepositoryImpl;
  let db: jest.Mocked<Kysely<DB>>;
  let trxMock: Record<string, jest.Mock>;
  let transactionMock: jest.Mock;
  let executeTransactionMock: jest.Mock;
  let insertIntoMock: jest.Mock;
  let valuesMock: jest.Mock;
  let onConflictMock: jest.Mock;
  let executeMock: jest.Mock;

  beforeEach(async () => {
    insertIntoMock = jest.fn().mockReturnThis();
    valuesMock = jest.fn().mockReturnThis();
    onConflictMock = jest.fn().mockReturnThis();
    executeMock = jest.fn().mockResolvedValue(undefined);

    trxMock = {
      insertInto: insertIntoMock,
      values: valuesMock,
      onConflict: onConflictMock,
      execute: executeMock,
    };

    executeTransactionMock = jest
      .fn()
      .mockImplementation(
        (cb: (trx: Record<string, jest.Mock>) => Promise<unknown>) =>
          cb(trxMock),
      );

    transactionMock = jest.fn().mockReturnValue({
      execute: executeTransactionMock,
    });

    db = {
      transaction: transactionMock,
    } as unknown as jest.Mocked<Kysely<DB>>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        TransactionWriteRepositoryImpl,
        {
          provide: DATABASE_CONNECTION,
          useValue: db,
        },
      ],
    }).compile();

    repository = module.get<TransactionWriteRepositoryImpl>(
      TransactionWriteRepositoryImpl,
    );
  });

  it('should save transaction and clear uncommitted events', async () => {
    const now = new Date();
    // Use build to create a transaction with an event
    const transaction = new TransactionBuilder()
      .withId('tx-123')
      .withAmount(100)
      .withFromAccountId('acc-from')
      .withToAccountId('acc-to')
      .withIdempotencyKey('idem-key')
      .withWorkspaceId('ws-123')
      .withCreatedAt(now)
      .withUpdatedAt(now)
      .withCreatedBy('user-1')
      .withUpdatedBy('user-1')
      .build('evt-123', now);

    expect(transaction.uncommittedEvents.length).toBe(1);

    const result = await repository.save(transaction);

    expect(result).toBe(transaction);
    expect(transactionMock).toHaveBeenCalled();
    expect(executeTransactionMock).toHaveBeenCalled();
    // One insert for transaction, one insert for outbox
    expect(insertIntoMock).toHaveBeenCalledWith('transaction');
    expect(insertIntoMock).toHaveBeenCalledWith('outbox');
    expect(valuesMock).toHaveBeenCalledTimes(2);
    expect(executeMock).toHaveBeenCalledTimes(2);
    // Events must be cleared after save
    expect(transaction.uncommittedEvents.length).toBe(0);
  });

  it('should save transaction without outbox insert if no events', async () => {
    const transaction = new TransactionBuilder()
      .withId('tx-123')
      .withAmount(100)
      .withFromAccountId('acc-from')
      .withToAccountId('acc-to')
      .withIdempotencyKey('idem-key')
      .withWorkspaceId('ws-123')
      .withCreatedBy('user-1')
      .withUpdatedBy('user-1')
      .reconstitute(); // Reconstitute doesn't apply a TransactionRecordedEvent

    expect(transaction.uncommittedEvents.length).toBe(0);

    const result = await repository.save(transaction);

    expect(result).toBe(transaction);
    expect(insertIntoMock).toHaveBeenCalledWith('transaction');
    expect(insertIntoMock).not.toHaveBeenCalledWith('outbox');
    expect(valuesMock).toHaveBeenCalledTimes(1);
    expect(executeMock).toHaveBeenCalledTimes(1);
  });
});
