export class DeleteAccountCommand {
  constructor(
    public readonly workspaceId: string,
    public readonly id: string,
    public readonly deletedBy: string,
  ) {}
}
