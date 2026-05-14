export class DeleteTransactionCommand {
  constructor(
    public readonly workspaceId: string,
    public readonly id: string,
    public readonly deletedBy: string,
  ) {}
}
