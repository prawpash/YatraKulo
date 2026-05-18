import { Test, TestingModule } from '@nestjs/testing';
import { AccountReadRepositoryImpl } from '@app/account/infrastructure/persistence/repository/AccountReadRepositoryImpl';
import { DATABASE_CONNECTION } from '@app/account/infrastructure/config/InjectionToken';
import { createDomainPageRequest } from '@yk/shared';
import { Kysely } from 'kysely';
import { DB } from '@app/account/infrastructure/config/db';

describe('AccountReadRepositoryImpl', () => {
  let repository: AccountReadRepositoryImpl;
  let db: jest.Mocked<Kysely<DB>>;
  let selectFromMock: jest.Mock;
  let executeTakeFirstMock: jest.Mock;
  let executeMock: jest.Mock;

  beforeEach(async () => {
    selectFromMock = jest.fn().mockReturnThis();
    executeTakeFirstMock = jest.fn();
    executeMock = jest.fn();

    db = {
      selectFrom: selectFromMock,
      selectAll: jest.fn().mockReturnThis(),
      where: jest.fn().mockReturnThis(),
      executeTakeFirst: executeTakeFirstMock,
      execute: executeMock,
      orderBy: jest.fn().mockReturnThis(),
      limit: jest.fn().mockReturnThis(),
      offset: jest.fn().mockReturnThis(),
      select: jest.fn().mockReturnThis(),
      fn: {
        count: jest.fn().mockReturnThis(),
      },
      as: jest.fn().mockReturnThis(),
    } as unknown as jest.Mocked<Kysely<DB>>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AccountReadRepositoryImpl,
        {
          provide: DATABASE_CONNECTION,
          useValue: db,
        },
      ],
    }).compile();

    repository = module.get<AccountReadRepositoryImpl>(
      AccountReadRepositoryImpl,
    );
  });

  it('should find by id and workspace id', async () => {
    const row = {
      id: 'acc-1',
      name: 'Test',
      type: 'ASSET',
      workspace_id: 'ws-1',
      created_at: new Date(),
      updated_at: new Date(),
    };
    executeTakeFirstMock.mockResolvedValue(row);

    const result = await repository.findByIdAndWorkspaceId('acc-1', 'ws-1');

    expect(result).toBeDefined();
    expect(result?.id).toBe('acc-1');
    expect(selectFromMock).toHaveBeenCalledWith('account');
  });

  it('should return null if not found', async () => {
    executeTakeFirstMock.mockResolvedValue(null);
    const result = await repository.findByIdAndWorkspaceId('acc-1', 'ws-1');
    expect(result).toBeNull();
  });

  it('should get paginated accounts', async () => {
    const pageRequest = createDomainPageRequest(0, 10);
    executeMock.mockResolvedValue([]); // content
    executeTakeFirstMock.mockResolvedValue({ count: 0 }); // count

    const result = await repository.getAccounts({
      workspaceId: 'ws-1',
      includeGlobal: false,
      pageRequest,
    });

    expect(result.content).toEqual([]);
    expect(result.totalElements).toBe(0);
  });

  it('should find by id', async () => {
    const row = {
      id: 'acc-1',
      name: 'Test',
      type: 'ASSET',
      workspace_id: null,
      created_at: new Date(),
      updated_at: new Date(),
    };
    executeTakeFirstMock.mockResolvedValue(row);

    const result = await repository.findById('acc-1');

    expect(result).toBeDefined();
    expect(result?.id).toBe('acc-1');
  });

  it('should throw if database error in getAccounts', async () => {
    const pageRequest = createDomainPageRequest(0, 10);
    executeMock.mockRejectedValue(new Error('DB Error'));

    await expect(
      repository.getAccounts({
        workspaceId: 'ws-1',
        includeGlobal: false,
        pageRequest,
      }),
    ).rejects.toThrow('DB Error');
  });
});
