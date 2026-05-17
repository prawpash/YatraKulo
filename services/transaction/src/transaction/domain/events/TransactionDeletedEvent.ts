import { IDomainEvent } from './IDomainEvent';

export class TransactionDeletedEvent implements IDomainEvent {
  public readonly schemaVersion: number = 1;

  constructor(
    public readonly eventId: string,
    public readonly occurredAt: Date,
    public readonly aggregateId: string,
    public readonly workspaceId: string,
  ) {}

  get eventType(): string {
    return 'TransactionDeleted';
  }
}

