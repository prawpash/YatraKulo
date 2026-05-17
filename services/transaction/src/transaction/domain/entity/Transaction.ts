import { DomainRuleViolationException } from '@yk/shared';
import { TransactionRecordedEvent } from '../events/TransactionRecordedEvent';
import { TransactionUpdatedEvent } from '../events/TransactionUpdatedEvent';
import { TransactionDeletedEvent } from '../events/TransactionDeletedEvent';
import { IDomainEvent } from '../events/IDomainEvent';

export class Transaction {
  private readonly _id: string;
  private _amount: number;
  private _note: string | null;
  private _fromAccountId: string;
  private _toAccountId: string;
  private readonly _idempotencyKey: string;
  private readonly _workspaceId: string;

  private readonly _createdAt: Date;
  private _updatedAt: Date;
  private _deletedAt: Date | null;

  private readonly _createdBy: string;
  private _updatedBy: string;
  private _deletedBy: string | null;

  private _domainEvents: IDomainEvent[] = [];

  private constructor(builder: TransactionBuilder) {
    if (!builder.id) {
      throw new DomainRuleViolationException('id is required');
    }
    if (builder.amount === undefined || builder.amount === null) {
      throw new DomainRuleViolationException('amount is required');
    }

    if (builder.amount < 0) {
      throw new DomainRuleViolationException('amount cannot be negative');
    }

    if (!builder.fromAccountId) {
      throw new DomainRuleViolationException('fromAccountId is required');
    }

    if (!builder.toAccountId) {
      throw new DomainRuleViolationException('toAccountId is required');
    }

    if (!builder.idempotencyKey) {
      throw new DomainRuleViolationException('idempotencyKey is required');
    }

    if (!builder.workspaceId) {
      throw new DomainRuleViolationException('workspaceId is required');
    }

    if (!builder.createdBy) {
      throw new DomainRuleViolationException('createdBy is required');
    }

    if (!builder.updatedBy) {
      throw new DomainRuleViolationException('updatedBy is required');
    }

    this._id = builder.id;
    this._amount = builder.amount;
    this._note = builder.note ?? null;
    this._fromAccountId = builder.fromAccountId;
    this._toAccountId = builder.toAccountId;
    this._idempotencyKey = builder.idempotencyKey;
    this._workspaceId = builder.workspaceId;

    this._createdAt = builder.createdAt;
    this._updatedAt = builder.updatedAt;
    this._deletedAt = builder.deletedAt ?? null;

    this._createdBy = builder.createdBy;
    this._updatedBy = builder.updatedBy;
    this._deletedBy = builder.deletedBy ?? null;
  }

  static create(
    builder: TransactionBuilder,
    eventId: string,
    occurredAt: Date,
  ): Transaction {
    const transaction = new Transaction(builder);
    transaction.apply(
      new TransactionRecordedEvent(
        eventId,
        occurredAt,
        transaction.id,
        transaction.workspaceId,
        transaction.amount,
        transaction.fromAccountId,
        transaction.toAccountId,
        transaction.note,
        transaction.idempotencyKey,
      ),
    );
    return transaction;
  }

  static reconstitute(builder: TransactionBuilder): Transaction {
    return new Transaction(builder);
  }

  get id(): string {
    return this._id;
  }

  get amount(): number {
    return this._amount;
  }

  get note(): string | null {
    return this._note;
  }

  get fromAccountId(): string {
    return this._fromAccountId;
  }

  get toAccountId(): string {
    return this._toAccountId;
  }

  get idempotencyKey(): string {
    return this._idempotencyKey;
  }

  get workspaceId(): string {
    return this._workspaceId;
  }

  get createdAt(): Date {
    return this._createdAt;
  }

  get updatedAt(): Date {
    return this._updatedAt;
  }

  get deletedAt(): Date | null {
    return this._deletedAt;
  }

  get createdBy(): string {
    return this._createdBy;
  }

  get updatedBy(): string {
    return this._updatedBy;
  }

  get deletedBy(): string | null {
    return this._deletedBy;
  }

  get uncommittedEvents(): IDomainEvent[] {
    return [...this._domainEvents];
  }

  protected apply(event: IDomainEvent): void {
    this._domainEvents.push(event);
  }

  clearEvents(): void {
    this._domainEvents = [];
  }

  update(
    params: {
      amount?: number;
      note?: string | null;
      updatedAt: Date;
      updatedBy: string;
    },
    eventId: string,
    occurredAt: Date,
  ): void {
    if (this._deletedAt !== null) {
      throw new DomainRuleViolationException('Transaction is already deleted');
    }

    if (params.amount !== undefined) {
      if (params.amount < 0) {
        throw new DomainRuleViolationException('amount cannot be negative');
      }
      this._amount = params.amount;
    }

    if (params.note !== undefined) {
      this._note = params.note;
    }

    this._updatedAt = params.updatedAt;
    this._updatedBy = params.updatedBy;

    this.apply(
      new TransactionUpdatedEvent(
        eventId,
        occurredAt,
        this.id,
        this.workspaceId,
        this.amount,
        this.fromAccountId,
        this.toAccountId,
        this.note,
        this.idempotencyKey,
      ),
    );
  }

  delete(
    deletedAt: Date,
    deletedBy: string,
    eventId: string,
    occurredAt: Date,
  ): void {
    if (this._deletedAt !== null) {
      throw new DomainRuleViolationException('Transaction is already deleted');
    }

    this._deletedAt = deletedAt;
    this._deletedBy = deletedBy;
    this._updatedAt = deletedAt;
    this._updatedBy = deletedBy;

    this.apply(
      new TransactionDeletedEvent(
        eventId,
        occurredAt,
        this.id,
        this.workspaceId,
      ),
    );
  }

  toPlainObject(): Record<string, unknown> {
    return {
      id: this._id,
      amount: this._amount,
      note: this._note,
      fromAccountId: this._fromAccountId,
      toAccountId: this._toAccountId,
      idempotencyKey: this._idempotencyKey,
      workspaceId: this._workspaceId,
      createdAt: this._createdAt,
      updatedAt: this._updatedAt,
      deletedAt: this._deletedAt,
      createdBy: this._createdBy,
      updatedBy: this._updatedBy,
      deletedBy: this._deletedBy,
    };
  }
}

export class TransactionBuilder {
  id: string | null = null;
  amount: number | null = null;
  note: string | null = null;
  fromAccountId: string | null = null;
  toAccountId: string | null = null;
  idempotencyKey: string | null = null;
  workspaceId: string | null = null;
  createdAt: Date = new Date();
  updatedAt: Date = new Date();
  deletedAt: Date | null = null;
  createdBy: string | null = null;
  updatedBy: string | null = null;
  deletedBy: string | null = null;

  withId(id: string): TransactionBuilder {
    this.id = id;
    return this;
  }

  withAmount(amount: number): TransactionBuilder {
    this.amount = amount;
    return this;
  }

  withNote(note: string | null): TransactionBuilder {
    this.note = note;
    return this;
  }

  withFromAccountId(fromAccountId: string): TransactionBuilder {
    this.fromAccountId = fromAccountId;
    return this;
  }

  withToAccountId(toAccountId: string): TransactionBuilder {
    this.toAccountId = toAccountId;
    return this;
  }

  withIdempotencyKey(idempotencyKey: string): TransactionBuilder {
    this.idempotencyKey = idempotencyKey;
    return this;
  }

  withWorkspaceId(workspaceId: string): TransactionBuilder {
    this.workspaceId = workspaceId;
    return this;
  }

  withCreatedAt(createdAt: Date): TransactionBuilder {
    this.createdAt = createdAt;
    return this;
  }

  withUpdatedAt(updatedAt: Date): TransactionBuilder {
    this.updatedAt = updatedAt;
    return this;
  }

  withDeletedAt(deletedAt: Date | null): TransactionBuilder {
    this.deletedAt = deletedAt;
    return this;
  }

  withCreatedBy(createdBy: string): TransactionBuilder {
    this.createdBy = createdBy;
    return this;
  }

  withUpdatedBy(updatedBy: string): TransactionBuilder {
    this.updatedBy = updatedBy;
    return this;
  }

  withDeletedBy(deletedBy: string | null): TransactionBuilder {
    this.deletedBy = deletedBy;
    return this;
  }

  build(eventId: string, occurredAt: Date): Transaction {
    return Transaction.create(this, eventId, occurredAt);
  }

  reconstitute(): Transaction {
    return Transaction.reconstitute(this);
  }
}
