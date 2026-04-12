import { AccountType } from '@app/account/domain/enums/AccountType';

export class CreateAccountCommand {
  constructor(
    public readonly name: string,
    public readonly type: AccountType,
    public readonly workspaceId: string,
    public readonly parentId: string | null,
    public readonly description: string | null,
    public readonly createdBy: string | null,
  ) {}
}
