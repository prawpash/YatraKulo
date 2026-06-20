import { Test, TestingModule } from '@nestjs/testing';
import { TransactionReadRepositoryImpl } from '@app/transaction/infrastructure/persistence/repository/TransactionReadRepositoryImpl';
import { DATABASE_CONNECTION } from '@app/transaction/infrastructure/config/InjectionToken';
import { createDomainPageRequest } from '@yk/shared';
import { Kysely } from 'kysely';
import { DB } from '@app/transaction/infrastructure/config/db';

describe('TransactionReadRepositoryImpl', () => {
  let repository: TransactionReadRepositoryImpl;
  let db: jest.Mocked<Kysely<DB>>;
  let selectFromMock: jest.Mock;
  let whereMock: jest.Mock;
  let executeTakeFirstMock: jest.Mock;
  let executeMock: jest.Mock;

  beforeEach(async () => {
    selectFromMock = jest.fn().mockReturnThis();
    whereMock = jest.fn().mockReturnThis();
    executeTakeFirstMock = jest.fn();
    executeMock = jest.fn();

    db = {
      selectFrom: selectFromMock,
      selectAll: jest.fn().mockReturnThis(),
      where: whereMock,
      executeTakeFirst: executeTakeFirstMock,
      execute: executeMock,
      orderBy: jest.fn().mockReturnThis(),
      limit: jest.fn().mockReturnThis(),
      offset: jest.fn().mockReturnThis(),
      select: jest.fn().mockReturnThis(),
    } as unknown as jest.Mocked<Kysely<DB>>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        TransactionReadRepositoryImpl,
        {
          provide: DATABASE_CONNECTION,
          useValue: db,
        },
      ],
    }).compile();

    repository = module.get<TransactionReadRepositoryImpl>(
      TransactionReadRepositoryImpl,
    );
  });

  describe('findById', () => {
    it('should find by id', async () => {
      const row = {
        id: 'tx-1',
        amount: '100',
        note: 'Note',
        from_account_id: 'acc-from',
        to_account_id: 'acc-to',
        idempotency_key: 'idem-key',
        workspace_id: 'ws-1',
        created_at: new Date(),
        updated_at: new Date(),
        deleted_at: null,
        created_by: 'user-1',
        updated_by: 'user-1',
        deleted_by: null,
      };
      executeTakeFirstMock.mockResolvedValue(row);

      const result = await repository.findById('tx-1');

      expect(result).toBeDefined();
      expect(result?.id).toBe('tx-1');
      expect(selectFromMock).toHaveBeenCalledWith('transaction');
      expect(whereMock).toHaveBeenCalledWith('id', '=', 'tx-1');
      expect(whereMock).toHaveBeenCalledWith('deleted_at', 'is', null);
    });

    it('should return null if not found', async () => {
      executeTakeFirstMock.mockResolvedValue(null);
      const result = await repository.findById('tx-1');
      expect(result).toBeNull();
    });
  });

  describe('findByIdAndWorkspaceId', () => {
    it('should find by id and workspace id', async () => {
      const row = {
        id: 'tx-1',
        amount: '100',
        note: 'Note',
        from_account_id: 'acc-from',
        to_account_id: 'acc-to',
        idempotency_key: 'idem-key',
        workspace_id: 'ws-1',
        created_at: new Date(),
        updated_at: new Date(),
        deleted_at: null,
        created_by: 'user-1',
        updated_by: 'user-1',
        deleted_by: null,
      };
      executeTakeFirstMock.mockResolvedValue(row);

      const result = await repository.findByIdAndWorkspaceId('tx-1', 'ws-1');

      expect(result).toBeDefined();
      expect(result?.id).toBe('tx-1');
      expect(selectFromMock).toHaveBeenCalledWith('transaction');
      expect(whereMock).toHaveBeenCalledWith('id', '=', 'tx-1');
      expect(whereMock).toHaveBeenCalledWith('workspace_id', '=', 'ws-1');
      expect(whereMock).toHaveBeenCalledWith('deleted_at', 'is', null);
    });
  });

  describe('findByIdempotencyKeyAndWorkspaceId', () => {
    it('should find by idempotency key and workspace id', async () => {
      const row = {
        id: 'tx-1',
        amount: '100',
        note: 'Note',
        from_account_id: 'acc-from',
        to_account_id: 'acc-to',
        idempotency_key: 'idem-key',
        workspace_id: 'ws-1',
        created_at: new Date(),
        updated_at: new Date(),
        deleted_at: null,
        created_by: 'user-1',
        updated_by: 'user-1',
        deleted_by: null,
      };
      executeTakeFirstMock.mockResolvedValue(row);

      const result = await repository.findByIdempotencyKeyAndWorkspaceId(
        'idem-key',
        'ws-1',
      );

      expect(result).toBeDefined();
      expect(result?.id).toBe('tx-1');
      expect(selectFromMock).toHaveBeenCalledWith('transaction');
      expect(whereMock).toHaveBeenCalledWith('idempotency_key', '=', 'idem-key');
      expect(whereMock).toHaveBeenCalledWith('workspace_id', '=', 'ws-1');
      expect(whereMock).toHaveBeenCalledWith('deleted_at', 'is', null);
    });
  });

  describe('getTransactions', () => {
    it('should return paginated transactions', async () => {
      const pageRequest = createDomainPageRequest(0, 10);
      executeMock.mockResolvedValue([]); // content
      executeTakeFirstMock.mockResolvedValue({ count: 0 }); // count

      const result = await repository.getTransactions({
        workspaceId: 'ws-1',
        pageRequest,
      });

      expect(result.content).toEqual([]);
      expect(result.totalElements).toBe(0);
      expect(selectFromMock).toHaveBeenCalledWith('transaction');
      expect(whereMock).toHaveBeenCalledWith('workspace_id', '=', 'ws-1');
      expect(whereMock).toHaveBeenCalledWith('deleted_at', 'is', null);
    });

    it('should filter by fromAccountId and toAccountId', async () => {
      const pageRequest = createDomainPageRequest(0, 10);
      executeMock.mockResolvedValue([]);
      executeTakeFirstMock.mockResolvedValue({ count: 0 });

      await repository.getTransactions({
        workspaceId: 'ws-1',
        fromAccountId: 'acc-1',
        toAccountId: 'acc-2',
        pageRequest,
      });

      expect(whereMock).toHaveBeenCalledWith('from_account_id', '=', 'acc-1');
      expect(whereMock).toHaveBeenCalledWith('to_account_id', '=', 'acc-2');
      expect(whereMock).toHaveBeenCalledWith('workspace_id', '=', 'ws-1');
      expect(whereMock).toHaveBeenCalledWith('deleted_at', 'is', null);
    });
  });
});
