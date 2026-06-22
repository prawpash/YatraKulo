import { TransactionBuilder } from '@app/transaction/domain/entity/Transaction';
import { DomainRuleViolationException } from '@yk/shared';

describe('Transaction Entity', () => {
  const mockDate = new Date();

  describe('creation validations', () => {
    it('should throw exception if id is missing', () => {
      const builder = new TransactionBuilder()
        .withAmount(100)
        .withFromAccountId('acc-from')
        .withToAccountId('acc-to')
        .withIdempotencyKey('idem')
        .withWorkspaceId('ws-1')
        .withCreatedBy('user-1')
        .withUpdatedBy('user-1');

      expect(() => builder.build('evt-1', mockDate)).toThrow(
        DomainRuleViolationException,
      );
    });

    it('should throw exception if amount is negative', () => {
      const builder = new TransactionBuilder()
        .withId('tx-1')
        .withAmount(-10)
        .withFromAccountId('acc-from')
        .withToAccountId('acc-to')
        .withIdempotencyKey('idem')
        .withWorkspaceId('ws-1')
        .withCreatedBy('user-1')
        .withUpdatedBy('user-1');

      expect(() => builder.build('evt-1', mockDate)).toThrow(
        DomainRuleViolationException,
      );
    });

    it('should successfully build a transaction with uncommitted events', () => {
      const tx = new TransactionBuilder()
        .withId('tx-1')
        .withAmount(100)
        .withFromAccountId('acc-from')
        .withToAccountId('acc-to')
        .withIdempotencyKey('idem')
        .withWorkspaceId('ws-1')
        .withCreatedBy('user-1')
        .withUpdatedBy('user-1')
        .build('evt-1', mockDate);

      expect(tx.id).toBe('tx-1');
      expect(tx.uncommittedEvents).toHaveLength(1);
      expect(tx.uncommittedEvents[0].eventType).toBe('TransactionRecorded');
    });
  });

  describe('update logic', () => {
    it('should throw exception if transaction is deleted', () => {
      const tx = new TransactionBuilder()
        .withId('tx-1')
        .withAmount(100)
        .withFromAccountId('acc-from')
        .withToAccountId('acc-to')
        .withIdempotencyKey('idem')
        .withWorkspaceId('ws-1')
        .withCreatedAt(mockDate)
        .withUpdatedAt(mockDate)
        .withDeletedAt(mockDate)
        .withCreatedBy('user-1')
        .withUpdatedBy('user-1')
        .reconstitute();

      expect(() =>
        tx.update(
          { amount: 200, updatedAt: mockDate, updatedBy: 'user-1' },
          'evt-2',
          mockDate,
        ),
      ).toThrow(DomainRuleViolationException);
    });

    it('should throw exception if updated amount is negative', () => {
      const tx = new TransactionBuilder()
        .withId('tx-1')
        .withAmount(100)
        .withFromAccountId('acc-from')
        .withToAccountId('acc-to')
        .withIdempotencyKey('idem')
        .withWorkspaceId('ws-1')
        .withCreatedAt(mockDate)
        .withUpdatedAt(mockDate)
        .withCreatedBy('user-1')
        .withUpdatedBy('user-1')
        .reconstitute();

      expect(() =>
        tx.update(
          { amount: -50, updatedAt: mockDate, updatedBy: 'user-1' },
          'evt-2',
          mockDate,
        ),
      ).toThrow(DomainRuleViolationException);
    });

    it('should update and append event if valid', () => {
      const tx = new TransactionBuilder()
        .withId('tx-1')
        .withAmount(100)
        .withFromAccountId('acc-from')
        .withToAccountId('acc-to')
        .withIdempotencyKey('idem')
        .withWorkspaceId('ws-1')
        .withCreatedAt(mockDate)
        .withUpdatedAt(mockDate)
        .withCreatedBy('user-1')
        .withUpdatedBy('user-1')
        .reconstitute();

      tx.update(
        {
          amount: 150,
          note: 'Updated note',
          updatedAt: mockDate,
          updatedBy: 'user-2',
        },
        'evt-2',
        mockDate,
      );

      expect(tx.amount).toBe(150);
      expect(tx.note).toBe('Updated note');
      expect(tx.updatedBy).toBe('user-2');
      expect(tx.uncommittedEvents).toHaveLength(1);
      expect(tx.uncommittedEvents[0].eventType).toBe('TransactionUpdated');
    });
  });

  describe('delete logic', () => {
    it('should throw exception if already deleted', () => {
      const tx = new TransactionBuilder()
        .withId('tx-1')
        .withAmount(100)
        .withFromAccountId('acc-from')
        .withToAccountId('acc-to')
        .withIdempotencyKey('idem')
        .withWorkspaceId('ws-1')
        .withCreatedAt(mockDate)
        .withUpdatedAt(mockDate)
        .withDeletedAt(mockDate)
        .withCreatedBy('user-1')
        .withUpdatedBy('user-1')
        .reconstitute();

      expect(() => tx.delete(mockDate, 'user-1', 'evt-2', mockDate)).toThrow(
        DomainRuleViolationException,
      );
    });

    it('should successfully soft delete transaction and record event', () => {
      const tx = new TransactionBuilder()
        .withId('tx-1')
        .withAmount(100)
        .withFromAccountId('acc-from')
        .withToAccountId('acc-to')
        .withIdempotencyKey('idem')
        .withWorkspaceId('ws-1')
        .withCreatedAt(mockDate)
        .withUpdatedAt(mockDate)
        .withCreatedBy('user-1')
        .withUpdatedBy('user-1')
        .reconstitute();

      tx.delete(mockDate, 'user-2', 'evt-2', mockDate);

      expect(tx.deletedAt).toEqual(mockDate);
      expect(tx.deletedBy).toBe('user-2');
      expect(tx.uncommittedEvents).toHaveLength(1);
      expect(tx.uncommittedEvents[0].eventType).toBe('TransactionDeleted');
    });
  });
});
