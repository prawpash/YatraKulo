export class TransactionDeletedEvent {
  constructor(
    public readonly id: string,
    public readonly workspaceId: string,
  ) {}
}
