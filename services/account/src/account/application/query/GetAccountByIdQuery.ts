export class GetAccountByIdQuery {
  constructor(
    public readonly workspaceId: string,
    public readonly id: string,
  ) {}
}
