import { Test, TestingModule } from '@nestjs/testing';
import { TransactionController } from '@app/transaction/interfaces/http/TransactionController';
import { CommandBus, QueryBus } from '@nestjs/cqrs';
import { BadRequestException } from '@nestjs/common';
import { CreateTransactionCommand } from '@app/transaction/application/command/CreateTransactionCommand';
import { UpdateTransactionCommand } from '@app/transaction/application/command/UpdateTransactionCommand';
import { DeleteTransactionCommand } from '@app/transaction/application/command/DeleteTransactionCommand';
import { GetTransactionByIdQuery } from '@app/transaction/application/query/GetTransactionByIdQuery';
import { GetTransactionsQuery } from '@app/transaction/application/query/GetTransactionsQuery';
import { TransactionBuilder } from '@app/transaction/domain/entity/Transaction';
import { createDomainPage, createDomainPageRequest } from '@yk/shared';
import type { JwtPayload } from '@app/transaction/infrastructure/auth/JwtStrategy';

describe('TransactionController', () => {
  let controller: TransactionController;
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
      controllers: [TransactionController],
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

    controller = module.get<TransactionController>(TransactionController);
  });

  describe('getTransactions', () => {
    it('should dispatch GetTransactionsQuery and return paginated results', async () => {
      const workspaceId = 'ws-123';
      const dto = {
        page: 0,
        size: 10,
        fromAccountId: 'acc-1',
        toAccountId: 'acc-2',
      };
      const pageRequest = createDomainPageRequest(0, 10);
      const transaction = new TransactionBuilder()
        .withId('tx-1')
        .withAmount(50)
        .withFromAccountId('acc-1')
        .withToAccountId('acc-2')
        .withIdempotencyKey('idem-1')
        .withWorkspaceId(workspaceId)
        .withCreatedBy('user-1')
        .withUpdatedBy('user-1')
        .reconstitute();

      const domainPage = createDomainPage([transaction], 1, 1, 0, 10);
      queryExecuteMock.mockResolvedValue(domainPage);

      const result = await controller.getTransactions(workspaceId, dto);

      expect(queryExecuteMock).toHaveBeenCalledWith(
        new GetTransactionsQuery(workspaceId, pageRequest, 'acc-1', 'acc-2'),
      );
      expect(result).toBeDefined();
      expect(result.content).toHaveLength(1);
      expect(result.content[0].id).toBe('tx-1');
    });
  });

  describe('getTransactionById', () => {
    it('should dispatch GetTransactionByIdQuery and return the transaction', async () => {
      const workspaceId = 'ws-123';
      const id = 'tx-1';
      const transaction = new TransactionBuilder()
        .withId(id)
        .withAmount(50)
        .withFromAccountId('acc-1')
        .withToAccountId('acc-2')
        .withIdempotencyKey('idem-1')
        .withWorkspaceId(workspaceId)
        .withCreatedBy('user-1')
        .withUpdatedBy('user-1')
        .reconstitute();

      queryExecuteMock.mockResolvedValue(transaction);

      const result = await controller.getTransactionById(workspaceId, id);

      expect(queryExecuteMock).toHaveBeenCalledWith(
        new GetTransactionByIdQuery(workspaceId, id),
      );
      expect(result).toBeDefined();
      expect(result.id).toBe(id);
    });
  });

  describe('createTransaction', () => {
    it('should throw BadRequestException if Idempotency-Key is missing', async () => {
      const workspaceId = 'ws-123';
      const user = { sub: 'user-1' } as unknown as JwtPayload;
      const dto = { amount: 100, fromAccountId: 'acc-1', toAccountId: 'acc-2' };

      await expect(
        controller.createTransaction(workspaceId, user, '', dto),
      ).rejects.toThrow(BadRequestException);
    });

    it('should dispatch CreateTransactionCommand and return the created transaction', async () => {
      const workspaceId = 'ws-123';
      const user = { sub: 'user-1' } as unknown as JwtPayload;
      const dto = {
        amount: 100,
        note: 'test',
        fromAccountId: 'acc-1',
        toAccountId: 'acc-2',
      };
      const transaction = new TransactionBuilder()
        .withId('tx-1')
        .withAmount(100)
        .withNote('test')
        .withFromAccountId('acc-1')
        .withToAccountId('acc-2')
        .withIdempotencyKey('idem-123')
        .withWorkspaceId(workspaceId)
        .withCreatedBy('user-1')
        .withUpdatedBy('user-1')
        .reconstitute();

      commandExecuteMock.mockResolvedValue(transaction);

      const result = await controller.createTransaction(
        workspaceId,
        user,
        'idem-123',
        dto,
      );

      expect(commandExecuteMock).toHaveBeenCalledWith(
        new CreateTransactionCommand(
          workspaceId,
          dto.amount,
          dto.note,
          dto.fromAccountId,
          dto.toAccountId,
          'idem-123',
          user.sub,
        ),
      );
      expect(result).toBeDefined();
      expect(result.id).toBe('tx-1');
    });
  });

  describe('updateTransaction', () => {
    it('should dispatch UpdateTransactionCommand', async () => {
      const workspaceId = 'ws-123';
      const id = 'tx-1';
      const user = { sub: 'user-1' } as unknown as JwtPayload;
      const dto = { amount: 200, note: 'updated' };

      commandExecuteMock.mockResolvedValue(undefined);

      await controller.updateTransaction(workspaceId, id, user, dto);

      expect(commandExecuteMock).toHaveBeenCalledWith(
        new UpdateTransactionCommand(workspaceId, id, user.sub, 200, 'updated'),
      );
    });
  });

  describe('deleteTransaction', () => {
    it('should dispatch DeleteTransactionCommand', async () => {
      const workspaceId = 'ws-123';
      const id = 'tx-1';
      const user = { sub: 'user-1' } as unknown as JwtPayload;

      commandExecuteMock.mockResolvedValue(undefined);

      await controller.deleteTransaction(workspaceId, id, user);

      expect(commandExecuteMock).toHaveBeenCalledWith(
        new DeleteTransactionCommand(workspaceId, id, user.sub),
      );
    });
  });
});
