import { TransactionMapper } from '@app/transaction/infrastructure/persistence/mapper/TransactionMapper';
import { TransactionBuilder } from '@app/transaction/domain/entity/Transaction';
import { Selectable } from 'kysely';
import { TransactionPersistence } from '@app/transaction/infrastructure/config/db';

describe('TransactionMapper', () => {
  const mockDate = new Date('2026-06-16T22:00:00.000Z');

  describe('toDomain', () => {
    it('should map a persistence row to a domain Transaction entity', () => {
      const row = {
        id: 'tx-123',
        amount: '150.50',
        note: 'Mock Note',
        from_account_id: 'acc-from',
        to_account_id: 'acc-to',
        idempotency_key: 'idem-123',
        workspace_id: 'ws-123',
        created_at: mockDate,
        updated_at: mockDate,
        deleted_at: null,
        created_by: 'user-1',
        updated_by: 'user-1',
        deleted_by: null,
      } as unknown as Selectable<TransactionPersistence>;

      const result = TransactionMapper.toDomain(row);

      expect(result).toBeDefined();
      expect(result.id).toBe('tx-123');
      expect(result.amount).toBe(150.5);
      expect(result.note).toBe('Mock Note');
      expect(result.fromAccountId).toBe('acc-from');
      expect(result.toAccountId).toBe('acc-to');
      expect(result.idempotencyKey).toBe('idem-123');
      expect(result.workspaceId).toBe('ws-123');
      expect(result.createdAt).toEqual(mockDate);
      expect(result.updatedAt).toEqual(mockDate);
      expect(result.deletedAt).toBeNull();
      expect(result.createdBy).toBe('user-1');
      expect(result.updatedBy).toBe('user-1');
      expect(result.deletedBy).toBeNull();
    });
  });

  describe('toPersistence', () => {
    it('should map a domain Transaction entity to a persistence object', () => {
      const transaction = new TransactionBuilder()
        .withId('tx-123')
        .withAmount(150.5)
        .withNote('Mock Note')
        .withFromAccountId('acc-from')
        .withToAccountId('acc-to')
        .withIdempotencyKey('idem-123')
        .withWorkspaceId('ws-123')
        .withCreatedAt(mockDate)
        .withUpdatedAt(mockDate)
        .withDeletedAt(null)
        .withCreatedBy('user-1')
        .withUpdatedBy('user-1')
        .withDeletedBy(null)
        .reconstitute();

      const result = TransactionMapper.toPersistence(transaction);

      expect(result).toBeDefined();
      expect(result.id).toBe('tx-123');
      expect(result.amount).toBe(150.5);
      expect(result.note).toBe('Mock Note');
      expect(result.from_account_id).toBe('acc-from');
      expect(result.to_account_id).toBe('acc-to');
      expect(result.idempotency_key).toBe('idem-123');
      expect(result.workspace_id).toBe('ws-123');
      expect(result.created_at).toEqual(mockDate);
      expect(result.updated_at).toEqual(mockDate);
      expect(result.deleted_at).toBeNull();
      expect(result.created_by).toBe('user-1');
      expect(result.updated_by).toBe('user-1');
      expect(result.deleted_by).toBeNull();
    });
  });
});
