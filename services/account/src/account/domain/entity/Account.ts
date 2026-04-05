import { DomainRuleViolationException } from '@yk/shared';
import { AccountType } from '@app/account/domain/enums/AccountType';

export class Account {
  private readonly _id: string;
  private _name: string;
  private _description: string | null;
  private _type: AccountType;
  private readonly _workspaceId: string | null;
  private _parentId: string | null;

  private readonly _createdAt: Date;
  private _updatedAt: Date;
  private _deletedAt: Date | null;

  private readonly _createdBy: string | null;
  private _updatedBy: string | null;
  private _deletedBy: string | null;

  private constructor(builder: AccountBuilder) {
    if (!builder.id) {
      throw new DomainRuleViolationException('id is required');
    }

    if (!builder.name || builder.name.trim().length === 0) {
      throw new DomainRuleViolationException('name must not be blank');
    }

    if (builder.name.length > 100) {
      throw new DomainRuleViolationException(
        'name must not exceed 100 characters',
      );
    }

    if (!builder.type) {
      throw new DomainRuleViolationException('type is required');
    }

    this._id = builder.id;
    this._name = builder.name;
    this._description = builder.description ?? null;
    this._type = builder.type;
    this._workspaceId = builder.workspaceId;
    this._parentId = builder.parentId ?? null;
    this._createdAt = builder.createdAt;
    this._updatedAt = builder.updatedAt;
    this._deletedAt = builder.deletedAt ?? null;
    this._createdBy = builder.createdBy ?? null;
    this._updatedBy = builder.updatedBy ?? null;
    this._deletedBy = builder.deletedBy ?? null;
  }

  static create(builder: AccountBuilder): Account {
    return new Account(builder);
  }

  rename(name: string, updatedAt: Date, updatedBy: string | null): void {
    if (this._deletedAt !== null) {
      throw new DomainRuleViolationException('Account is already deleted');
    }
    if (!name || name.trim().length === 0) {
      throw new DomainRuleViolationException('name must not be blank');
    }
    if (name.length > 100) {
      throw new DomainRuleViolationException('name must not exceed 100 characters');
    }
    this._name = name;
    this._updatedAt = updatedAt;
    if (updatedBy) {
      this._updatedBy = updatedBy;
    }
  }

  changeDescription(
    description: string | null,
    updatedAt: Date,
    updatedBy: string | null,
  ): void {
    if (this._deletedAt !== null) {
      throw new DomainRuleViolationException('Account is already deleted');
    }
    this._description = description;
    this._updatedAt = updatedAt;
    if (updatedBy) {
      this._updatedBy = updatedBy;
    }
  }

  changeParent(
    parentId: string | null,
    updatedAt: Date,
    updatedBy: string | null,
  ): void {
    if (this._deletedAt !== null) {
      throw new DomainRuleViolationException('Account is already deleted');
    }
    this._parentId = parentId;
    this._updatedAt = updatedAt;
    if (updatedBy) {
      this._updatedBy = updatedBy;
    }
  }

  changeType(
    type: AccountType,
    updatedAt: Date,
    updatedBy: string | null,
  ): void {
    if (this._deletedAt !== null) {
      throw new DomainRuleViolationException('Account is already deleted');
    }
    if (!type) {
      throw new DomainRuleViolationException('type is required');
    }
    this._type = type;
    this._updatedAt = updatedAt;
    if (updatedBy) {
      this._updatedBy = updatedBy;
    }
  }

  delete(deletedAt: Date, deletedBy: string): void {
    if (this._deletedAt !== null) {
      throw new DomainRuleViolationException('Account is already deleted');
    }
    if (!deletedBy) {
      throw new DomainRuleViolationException('deletedBy must not be null');
    }
    this._updatedAt = deletedAt;
    this._deletedAt = deletedAt;
    this._updatedBy = deletedBy;
    this._deletedBy = deletedBy;
  }

  get id(): string {
    return this._id;
  }

  get name(): string {
    return this._name;
  }

  get description(): string | null {
    return this._description;
  }

  get type(): AccountType {
    return this._type;
  }

  get workspaceId(): string | null {
    return this._workspaceId;
  }

  get parentId(): string | null {
    return this._parentId;
  }

  get createdAt(): Date {
    return this._createdAt;
  }

  get updatedAt(): Date {
    return this._updatedAt;
  }

  get deletedAt(): Date | null {
    return this._deletedAt;
  }

  get createdBy(): string | null {
    return this._createdBy;
  }

  get updatedBy(): string | null {
    return this._updatedBy;
  }

  get deletedBy(): string | null {
    return this._deletedBy;
  }

  toPlainObject(): Record<string, unknown> {
    return {
      id: this._id,
      name: this._name,
      description: this._description,
      type: this._type,
      workspaceId: this._workspaceId,
      parentId: this._parentId,
      createdAt: this._createdAt,
      updatedAt: this._updatedAt,
      deletedAt: this._deletedAt,
      createdBy: this._createdBy,
      updatedBy: this._updatedBy,
      deletedBy: this._deletedBy,
    };
  }
}

export class AccountBuilder {
  id: string | null = null;
  name: string = '';
  description: string | null = null;
  type: AccountType | null = null;
  workspaceId: string | null = null;
  parentId: string | null = null;
  createdAt: Date = new Date();
  updatedAt: Date = new Date();
  deletedAt: Date | null = null;
  createdBy: string | null = null;
  updatedBy: string | null = null;
  deletedBy: string | null = null;

  withId(id: string): AccountBuilder {
    this.id = id;
    return this;
  }

  withName(name: string): AccountBuilder {
    this.name = name;
    return this;
  }

  withDescription(description: string | null): AccountBuilder {
    this.description = description;
    return this;
  }

  withType(type: AccountType): AccountBuilder {
    this.type = type;
    return this;
  }

  withWorkspaceId(workspaceId: string | null): AccountBuilder {
    this.workspaceId = workspaceId;
    return this;
  }

  withParentId(parentId: string | null): AccountBuilder {
    this.parentId = parentId;
    return this;
  }

  withCreatedAt(createdAt: Date): AccountBuilder {
    this.createdAt = createdAt;
    return this;
  }

  withUpdatedAt(updatedAt: Date): AccountBuilder {
    this.updatedAt = updatedAt;
    return this;
  }

  withDeletedAt(deletedAt: Date | null): AccountBuilder {
    this.deletedAt = deletedAt;
    return this;
  }

  withCreatedBy(createdBy: string | null): AccountBuilder {
    this.createdBy = createdBy;
    return this;
  }

  withUpdatedBy(updatedBy: string | null): AccountBuilder {
    this.updatedBy = updatedBy;
    return this;
  }

  withDeletedBy(deletedBy: string | null): AccountBuilder {
    this.deletedBy = deletedBy;
    return this;
  }

  build(): Account {
    return Account.create(this);
  }
}
