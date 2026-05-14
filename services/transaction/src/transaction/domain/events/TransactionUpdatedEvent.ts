import { IDomainEvent } from './IDomainEvent';

export class TransactionUpdatedEvent implements IDomainEvent {
  public readonly schemaVersion: number = 1;

  constructor(
    public readonly eventId: string,
    public readonly occurredAt: Date,
    public readonly aggregateId: string,
    public readonly workspaceId: string,
    public readonly amount: number,
    public readonly fromAccountId: string,
    public readonly toAccountId: string,
    public readonly note: string | null,
    public readonly idempotencyKey: string,
  ) {}

  get eventType(): string {
    return 'TransactionUpdated';
  }
}

