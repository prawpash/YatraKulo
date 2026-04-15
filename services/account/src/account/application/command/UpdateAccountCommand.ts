import { AccountType } from '@app/account/domain/enums/AccountType';

export class UpdateAccountCommand {
  constructor(
    public readonly workspaceId: string,
    public readonly id: string,
    public readonly updatedBy: string | null,
    public readonly name?: string,
    public readonly description?: string | null,
    public readonly parentId?: string | null,
    public readonly type?: AccountType,
  ) {}
}
