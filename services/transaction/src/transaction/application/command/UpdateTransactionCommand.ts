export class UpdateTransactionCommand {
  constructor(
    public readonly workspaceId: string,
    public readonly id: string,
    public readonly updatedBy: string,
    public readonly amount?: number,
    public readonly note?: string | null,
  ) {}
}
