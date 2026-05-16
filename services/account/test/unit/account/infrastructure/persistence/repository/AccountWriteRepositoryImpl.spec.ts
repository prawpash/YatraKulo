import { Test, TestingModule } from '@nestjs/testing';
import { AccountWriteRepositoryImpl } from '@app/account/infrastructure/persistence/repository/AccountWriteRepositoryImpl';
import { DATABASE_CONNECTION } from '@app/account/infrastructure/config/InjectionToken';
import { AccountBuilder } from '@app/account/domain/entity/Account';
import { AccountType } from '@app/account/domain/enums/AccountType';
import { Kysely } from 'kysely';
import { DB } from '@app/account/infrastructure/config/db';

describe('AccountWriteRepositoryImpl', () => {
  let repository: AccountWriteRepositoryImpl;
  let db: jest.Mocked<Kysely<DB>>;
  let insertIntoMock: jest.Mock;
  let valuesMock: jest.Mock;
  let deleteFromMock: jest.Mock;
  let whereMock: jest.Mock;
  let executeMock: jest.Mock;

  beforeEach(async () => {
    insertIntoMock = jest.fn().mockReturnThis();
    valuesMock = jest.fn().mockReturnThis();
    deleteFromMock = jest.fn().mockReturnThis();
    whereMock = jest.fn().mockReturnThis();
    executeMock = jest.fn().mockResolvedValue(undefined);

    db = {
      insertInto: insertIntoMock,
      values: valuesMock,
      onConflict: jest.fn().mockReturnThis(),
      execute: executeMock,
      deleteFrom: deleteFromMock,
      where: whereMock,
    } as unknown as jest.Mocked<Kysely<DB>>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AccountWriteRepositoryImpl,
        {
          provide: DATABASE_CONNECTION,
          useValue: db,
        },
      ],
    }).compile();

    repository = module.get<AccountWriteRepositoryImpl>(
      AccountWriteRepositoryImpl,
    );
  });

  it('should save an account', async () => {
    const account = new AccountBuilder()
      .withId('acc-1')
      .withName('Test')
      .withType(AccountType.ASSET)
      .build();

    await repository.save(account);

    expect(insertIntoMock).toHaveBeenCalledWith('account');
    expect(valuesMock).toHaveBeenCalled();
    expect(executeMock).toHaveBeenCalled();
  });

  it('should delete an account', async () => {
    await repository.delete('ws-1', 'acc-1');

    expect(deleteFromMock).toHaveBeenCalledWith('account');
    expect(whereMock).toHaveBeenCalledTimes(2);
    expect(executeMock).toHaveBeenCalled();
  });
});
