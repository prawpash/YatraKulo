export class CreateTransactionCommand {
  constructor(
    public readonly workspaceId: string,
    public readonly amount: number,
    public readonly note: string | null,
    public readonly fromAccountId: string,
    public readonly toAccountId: string,
    public readonly idempotencyKey: string,
    public readonly createdBy: string,
  ) {}
}
