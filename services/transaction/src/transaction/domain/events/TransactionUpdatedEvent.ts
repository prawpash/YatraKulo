export class TransactionUpdatedEvent {
  constructor(
    public readonly id: string,
    public readonly amount: number,
    public readonly note: string | null,
    public readonly fromAccountId: string,
    public readonly toAccountId: string,
    public readonly idempotencyKey: string,
    public readonly workspaceId: string,
  ) {}
}
