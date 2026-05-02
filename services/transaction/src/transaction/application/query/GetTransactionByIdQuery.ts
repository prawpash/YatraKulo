export class GetTransactionByIdQuery {
  constructor(
    public readonly workspaceId: string,
    public readonly id: string,
  ) {}
}
