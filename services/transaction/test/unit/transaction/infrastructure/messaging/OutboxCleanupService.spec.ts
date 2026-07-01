import { Test, TestingModule } from '@nestjs/testing';
import { OutboxCleanupService } from '@app/shared/messaging/OutboxCleanupService';
import { DATABASE_CONNECTION } from '@app/shared/config/InjectionToken';
import { ConfigService } from '@nestjs/config';
import { Kysely } from 'kysely';
import { DB } from '@app/shared/config/db';
import { OutboxStatus } from '@app/shared/messaging/OutboxStatus';

describe('OutboxCleanupService', () => {
  let service: OutboxCleanupService;
  let db: jest.Mocked<Kysely<DB>>;
  let configService: jest.Mocked<ConfigService>;
  let deleteFromMock: jest.Mock;
  let whereMock: jest.Mock;
  let executeTakeFirstMock: jest.Mock;
  let configGetMock: jest.Mock;

  beforeEach(async () => {
    deleteFromMock = jest.fn().mockReturnThis();
    whereMock = jest.fn().mockReturnThis();
    executeTakeFirstMock = jest.fn();
    configGetMock = jest.fn().mockReturnValue(7);

    db = {
      deleteFrom: deleteFromMock,
      where: whereMock,
      executeTakeFirst: executeTakeFirstMock,
    } as unknown as jest.Mocked<Kysely<DB>>;

    configService = {
      get: configGetMock,
    } as unknown as jest.Mocked<ConfigService>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        OutboxCleanupService,
        {
          provide: DATABASE_CONNECTION,
          useValue: db,
        },
        {
          provide: ConfigService,
          useValue: configService,
        },
      ],
    }).compile();

    service = module.get<OutboxCleanupService>(OutboxCleanupService);
  });

  it('should delete published outbox events older than the retention threshold', async () => {
    executeTakeFirstMock.mockResolvedValue({ numDeletedRows: BigInt(5) });

    await service.handleCron();

    expect(configGetMock).toHaveBeenCalledWith('outboxRetentionDays');
    expect(deleteFromMock).toHaveBeenCalledWith('outbox');
    expect(whereMock).toHaveBeenCalledWith(
      'status',
      '=',
      OutboxStatus.PUBLISHED,
    );
    expect(whereMock).toHaveBeenCalledWith(
      'published_at',
      '<',
      expect.any(Date),
    );
    expect(executeTakeFirstMock).toHaveBeenCalled();
  });
});
