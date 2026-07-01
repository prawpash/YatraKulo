import { Test, TestingModule } from '@nestjs/testing';
import { AccountController } from '@app/account/interfaces/http/AccountController';
import { CommandBus, QueryBus } from '@nestjs/cqrs';
import { AccountType } from '@app/account/domain/enums/AccountType';
import { AccountBuilder } from '@app/account/domain/entity/Account';
import { createDomainPage } from '@yk/shared';
import { GetAccountsDto } from '@app/account/interfaces/http/dto/GetAccountsDto';
import { CreateAccountDto } from '@app/account/interfaces/http/dto/CreateAccountDto';
import { UpdateAccountDto } from '@app/account/interfaces/http/dto/UpdateAccountDto';
import { JwtPayload } from '@app/shared/auth/JwtStrategy';

describe('AccountController', () => {
  let controller: AccountController;
  let commandBus: jest.Mocked<CommandBus>;
  let queryBus: jest.Mocked<QueryBus>;
  let commandExecuteMock: jest.Mock;
  let queryExecuteMock: jest.Mock;

  beforeEach(async () => {
    commandExecuteMock = jest.fn();
    queryExecuteMock = jest.fn();
    commandBus = {
      execute: commandExecuteMock,
    } as unknown as jest.Mocked<CommandBus>;
    queryBus = {
      execute: queryExecuteMock,
    } as unknown as jest.Mocked<QueryBus>;

    const module: TestingModule = await Test.createTestingModule({
      controllers: [AccountController],
      providers: [
        {
          provide: CommandBus,
          useValue: commandBus,
        },
        {
          provide: QueryBus,
          useValue: queryBus,
        },
      ],
    }).compile();

    controller = module.get<AccountController>(AccountController);
  });

  it('should get accounts', async () => {
    const workspaceId = 'ws-1';
    const queryDto = new GetAccountsDto();
    queryDto.page = 1;
    queryDto.size = 10;

    const domainPage = createDomainPage([], 0, 0, 1, 10);
    queryExecuteMock.mockResolvedValue(domainPage);

    const result = await controller.getAccounts(workspaceId, queryDto);

    expect(result).toBeDefined();
    expect(queryExecuteMock).toHaveBeenCalled();
  });

  it('should get account by id', async () => {
    const workspaceId = 'ws-1';
    const accountId = 'acc-1';
    const account = new AccountBuilder()
      .withId(accountId)
      .withName('Test')
      .withType(AccountType.ASSET)
      .build();
    queryExecuteMock.mockResolvedValue(account);

    const result = await controller.getAccountById(workspaceId, accountId);

    expect(result.id).toBe(accountId);
    expect(queryExecuteMock).toHaveBeenCalled();
  });

  it('should create account', async () => {
    const workspaceId = 'ws-1';
    const user = { sub: 'user-1', email: 'test@test.com' };
    const dto = new CreateAccountDto();
    dto.name = 'New Account';
    dto.type = AccountType.ASSET;

    const account = new AccountBuilder()
      .withId('new-id')
      .withName(dto.name)
      .withType(dto.type)
      .build();
    commandExecuteMock.mockResolvedValue(account);

    const result = await controller.createAccount(
      workspaceId,
      user as JwtPayload,
      dto,
    );

    expect(result.id).toBe('new-id');
    expect(commandExecuteMock).toHaveBeenCalled();
  });

  it('should update account', async () => {
    const workspaceId = 'ws-1';
    const accountId = 'acc-1';
    const user = { sub: 'user-1', email: 'test@test.com' };
    const dto = new UpdateAccountDto();
    dto.name = 'Updated Name';

    await controller.updateAccount(
      workspaceId,
      accountId,
      user as JwtPayload,
      dto,
    );

    expect(commandExecuteMock).toHaveBeenCalled();
  });

  it('should delete account', async () => {
    const workspaceId = 'ws-1';
    const accountId = 'acc-1';
    const user = { sub: 'user-1', email: 'test@test.com' };

    await controller.deleteAccount(workspaceId, accountId, user as JwtPayload);

    expect(commandExecuteMock).toHaveBeenCalled();
  });
});
