import { Inject, Injectable } from '@nestjs/common';
import { Kysely, SelectQueryBuilder } from 'kysely';
import { Transaction } from '@app/transaction/domain/entity/Transaction';
import {
  GetTransactionsParams,
  TransactionReadRepository,
} from '@app/transaction/domain/repository/TransactionReadRepository';
import { DATABASE_CONNECTION } from '@app/transaction/infrastructure/config/InjectionToken';
import { DB } from '@app/transaction/infrastructure/config/db';
import { TransactionMapper } from '@app/transaction/infrastructure/persistence/mapper/TransactionMapper';
import { DomainPage, DomainPageRequest, createDomainPage } from '@yk/shared';

@Injectable()
export class TransactionReadRepositoryImpl implements TransactionReadRepository {
  constructor(
    @Inject(DATABASE_CONNECTION)
    private readonly db: Kysely<DB>,
  ) {}

  async findById(id: string): Promise<Transaction | null> {
    const result = await this.db
      .selectFrom('transaction')
      .selectAll()
      .where('id', '=', id)
      .where('deleted_at', 'is', null)
      .executeTakeFirst();

    return result ? TransactionMapper.toDomain(result) : null;
  }

  async findByIdAndWorkspaceId(
    id: string,
    workspaceId: string,
  ): Promise<Transaction | null> {
    const result = await this.db
      .selectFrom('transaction')
      .selectAll()
      .where('id', '=', id)
      .where('workspace_id', '=', workspaceId)
      .where('deleted_at', 'is', null)
      .executeTakeFirst();

    return result ? TransactionMapper.toDomain(result) : null;
  }

  async findByIdempotencyKeyAndWorkspaceId(
    idempotencyKey: string,
    workspaceId: string,
  ): Promise<Transaction | null> {
    const result = await this.db
      .selectFrom('transaction')
      .selectAll()
      .where('idempotency_key', '=', idempotencyKey)
      .where('workspace_id', '=', workspaceId)
      .where('deleted_at', 'is', null)
      .executeTakeFirst();

    return result ? TransactionMapper.toDomain(result) : null;
  }

  async getTransactions(
    params: GetTransactionsParams,
  ): Promise<DomainPage<Transaction>> {
    let query = this.db
      .selectFrom('transaction')
      .where('workspace_id', '=', params.workspaceId);

    if (params.fromAccountId) {
      query = query.where('from_account_id', '=', params.fromAccountId);
    }
    if (params.toAccountId) {
      query = query.where('to_account_id', '=', params.toAccountId);
    }

    return this.getPaginatedResult(query, params.pageRequest);
  }

  private async getPaginatedResult(
    // eslint-disable-next-line @typescript-eslint/no-empty-object-type
    query: SelectQueryBuilder<DB, 'transaction', {}>,
    pageRequest: DomainPageRequest,
  ): Promise<DomainPage<Transaction>> {
    const { page, size } = pageRequest;

    const [contentRaw, countResult] = await Promise.all([
      query
        .selectAll()
        .where('deleted_at', 'is', null)
        .orderBy('created_at', 'desc')
        .limit(size)
        .offset(page * size)
        .execute(),
      query
        .where('deleted_at', 'is', null)
        .select((eb) => eb.fn.count('id').as('count'))
        .executeTakeFirst(),
    ]);

    const content = contentRaw.map((row) => TransactionMapper.toDomain(row));
    const totalElements = Number(countResult?.count ?? 0);
    const totalPages = Math.ceil(totalElements / size);

    return createDomainPage(content, totalElements, totalPages, page, size);
  }
}
