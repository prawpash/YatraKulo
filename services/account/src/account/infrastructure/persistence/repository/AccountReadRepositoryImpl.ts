import { Inject, Injectable } from '@nestjs/common';
import { Kysely } from 'kysely';
import { Account } from '@app/account/domain/entity/Account';
import { AccountReadRepository, GetAccountsParams } from '@app/account/domain/repository/AccountReadRepository';
import { DomainPage, DomainPageRequest, createDomainPage } from '@yk/shared';
import { DATABASE_CONNECTION } from '@app/account/infrastructure/config/InjectionToken';
import { DB } from '@app/account/infrastructure/config/db';
import { AccountMapper } from '@app/account/infrastructure/persistence/mapper/AccountMapper';

@Injectable()
export class AccountReadRepositoryImpl implements AccountReadRepository {
  constructor(
    @Inject(DATABASE_CONNECTION)
    private readonly db: Kysely<DB>,
  ) { }

  async findById(id: string): Promise<Account | null> {
    const result = await this.db
      .selectFrom('account')
      .selectAll()
      .where('id', '=', id)
      .executeTakeFirst();

    return result ? AccountMapper.toDomain(result) : null;
  }

  async getAccounts({
    workspaceId,
    includeGlobal,
    searchTerm,
    parentId,
    pageRequest,
  }: GetAccountsParams): Promise<DomainPage<Account>> {
    let query = this.db.selectFrom('account');

    if (workspaceId) {
      if (includeGlobal) {
        query = query.where((eb) =>
          eb.or([
            eb('workspace_id', '=', workspaceId),
            eb('workspace_id', 'is', null),
          ]),
        );
      } else {
        query = query.where('workspace_id', '=', workspaceId);
      }
    } else {
      query = query.where('workspace_id', 'is', null);
    }

    if (searchTerm) {
      query = query.where('name', 'ilike', `%${searchTerm}%`);
    }

    if (parentId !== undefined) {
      if (parentId === null) {
        query = query.where('parent_id', 'is', null);
      } else {
        query = query.where('parent_id', '=', parentId);
      }
    }

    return this.getPaginatedResult(query, pageRequest);
  }

  private async getPaginatedResult(
    query: any,
    pageRequest: DomainPageRequest,
  ): Promise<DomainPage<Account>> {
    const { page, size } = pageRequest;

    const [contentRaw, countResult] = await Promise.all([
      query
        .selectAll()
        .limit(size)
        .offset(page * size)
        .execute(),
      query
        .select((eb: any) => eb.fn.count('id').as('count'))
        .executeTakeFirst(),
    ]);

    const content = contentRaw.map(AccountMapper.toDomain);
    const totalElements = Number(countResult?.count ?? 0);
    const totalPages = Math.ceil(totalElements / size);

    return createDomainPage(
      content,
      totalElements,
      totalPages,
      page,
      size,
    );
  }
}
