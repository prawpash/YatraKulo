import { Test, TestingModule } from '@nestjs/testing';
import { OutboxRelayService } from '@app/shared/messaging/OutboxRelayService';
import { DATABASE_CONNECTION } from '@app/shared/config/InjectionToken';
import { ClientProxy } from '@nestjs/microservices';
import { Kysely } from 'kysely';
import { DB } from '@app/shared/config/db';
import { OutboxStatus } from '@app/shared/messaging/OutboxStatus';
import { of, throwError } from 'rxjs';

describe('OutboxRelayService', () => {
  let service: OutboxRelayService;
  let db: jest.Mocked<Kysely<DB>>;
  let client: jest.Mocked<ClientProxy>;
  let selectFromMock: jest.Mock;
  let executeMock: jest.Mock;
  let updateTableMock: jest.Mock;
  let setMock: jest.Mock;
  let clientEmitMock: jest.Mock;

  beforeEach(async () => {
    selectFromMock = jest.fn().mockReturnThis();
    executeMock = jest.fn();
    updateTableMock = jest.fn().mockReturnThis();
    setMock = jest.fn().mockReturnThis();

    db = {
      selectFrom: selectFromMock,
      selectAll: jest.fn().mockReturnThis(),
      where: jest.fn().mockReturnThis(),
      orderBy: jest.fn().mockReturnThis(),
      limit: jest.fn().mockReturnThis(),
      execute: executeMock,
      updateTable: updateTableMock,
      set: setMock,
    } as unknown as jest.Mocked<Kysely<DB>>;

    clientEmitMock = jest.fn();
    client = {
      emit: clientEmitMock,
    } as unknown as jest.Mocked<ClientProxy>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        OutboxRelayService,
        {
          provide: DATABASE_CONNECTION,
          useValue: db,
        },
        {
          provide: 'RABBITMQ_SERVICE',
          useValue: client,
        },
      ],
    }).compile();

    service = module.get<OutboxRelayService>(OutboxRelayService);
  });

  it('should do nothing if there are no pending events', async () => {
    executeMock.mockResolvedValue([]);

    await service.handleCron();

    expect(clientEmitMock).not.toHaveBeenCalled();
  });

  it('should publish events and mark them as PUBLISHED', async () => {
    interface OutboxEvent {
      id: string;
      event_type: string;
      payload: { amount: number };
    }
    const mockEvents: OutboxEvent[] = [
      {
        id: '1',
        event_type: 'TransactionRecordedEvent',
        payload: { amount: 100 },
      },
    ];
    executeMock.mockResolvedValueOnce(mockEvents); // for selectFrom
    executeMock.mockResolvedValue(undefined); // for updateTable
    clientEmitMock.mockReturnValue(of(undefined));

    await service.handleCron();

    expect(clientEmitMock).toHaveBeenCalledWith('TransactionRecordedEvent', {
      amount: 100,
    });
    expect(updateTableMock).toHaveBeenCalledWith('outbox');
    expect(setMock).toHaveBeenCalledWith({
      status: OutboxStatus.PUBLISHED,
      published_at: expect.any(Date) as unknown,
    });
  });

  it('should mark event as FAILED if publishing fails', async () => {
    interface OutboxEvent {
      id: string;
      event_type: string;
      payload: { amount: number };
    }
    const mockEvents: OutboxEvent[] = [
      {
        id: '1',
        event_type: 'TransactionRecordedEvent',
        payload: { amount: 100 },
      },
    ];
    executeMock.mockResolvedValueOnce(mockEvents);
    executeMock.mockResolvedValue(undefined);
    clientEmitMock.mockReturnValue(
      throwError(() => new Error('RabbitMQ error')),
    );

    await service.handleCron();

    expect(clientEmitMock).toHaveBeenCalled();
    expect(updateTableMock).toHaveBeenCalledWith('outbox');
    expect(setMock).toHaveBeenCalledWith({
      status: OutboxStatus.FAILED,
    });
  });
});
