import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import request from 'supertest';
import { AppModule } from '../../src/app.module';
import { TestcontainersSetup } from '../utils/TestcontainersSetup';
import { JwtAuthGuard } from '../../src/transaction/infrastructure/auth/JwtAuthGuard';
import { PermissionsGuard } from '../../src/transaction/infrastructure/auth/PermissionsGuard';
import {
  MockJwtAuthGuard,
  MockPermissionsGuard,
} from '../utils/MockAuthGuards';
import { v4 as uuidv4 } from 'uuid';
import { JwtStrategy } from '@app/transaction/infrastructure/auth/JwtStrategy';
import { TransactionPageResponseDto } from '@app/transaction/interfaces/http/dto/TransactionPageResponseDto';
import { TransactionResponseDto } from '@app/transaction/interfaces/http/dto/TransactionResponseDto';
import { GlobalExceptionFilter } from '../../src/GlobalExceptionFilter';
import { OutboxRelayService } from '@app/transaction/infrastructure/messaging/OutboxRelayService';
import { Kysely } from 'kysely';
import { DATABASE_CONNECTION } from '@app/transaction/infrastructure/config/InjectionToken';
import { DB } from '@app/transaction/infrastructure/config/db';

describe('TransactionController (Integration)', () => {
  let app: INestApplication;
  let outboxRelayService: OutboxRelayService;
  let db: Kysely<DB>;

  beforeAll(async () => {
    // Start Testcontainers and apply migrations
    await TestcontainersSetup.start();

    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    })
      .overrideGuard(JwtAuthGuard)
      .useClass(MockJwtAuthGuard)
      .overrideGuard(PermissionsGuard)
      .useClass(MockPermissionsGuard)
      .overrideProvider(JwtStrategy)
      .useValue({ onModuleInit: jest.fn() })
      .compile();

    app = moduleFixture.createNestApplication();
    app.useGlobalPipes(
      new ValidationPipe({ transform: true, whitelist: true }),
    );
    app.useGlobalFilters(new GlobalExceptionFilter());

    await app.init();

    outboxRelayService = app.get<OutboxRelayService>(OutboxRelayService);
    db = app.get<Kysely<DB>>(DATABASE_CONNECTION);
  }, 120000); // 2 minutes timeout for container pull and startup

  afterAll(async () => {
    if (app) {
      await app.close(); // Important to stop ScheduleModule crons
    }
    await TestcontainersSetup.stop();
  });

  afterEach(async () => {
    // Clean up tables to ensure test isolation
    await db.deleteFrom('outbox').execute();
    await db.deleteFrom('transaction').execute();
  });

  describe('POST /transactions', () => {
    it('should create a new transaction and publish outbox event', async () => {
      const workspaceId = uuidv4();
      const idempotencyKey = uuidv4();
      const payload = {
        amount: 100,
        note: 'Test Integration Transaction',
        fromAccountId: uuidv4(),
        toAccountId: uuidv4(),
      };

      const response = await request(app.getHttpServer())
        .post('/transactions')
        .set('X-Workspace-Id', workspaceId)
        .set('Idempotency-Key', idempotencyKey)
        .send(payload);

      if (response.status !== 201) {
        console.log('Error creating transaction:', response.body);
      }
      expect(response.status).toBe(201);

      const responseBody = response.body as TransactionResponseDto;

      expect(responseBody.amount).toBe(payload.amount);
      expect(responseBody.note).toBe(payload.note);
      expect(responseBody.fromAccountId).toBe(payload.fromAccountId);
      expect(responseBody.toAccountId).toBe(payload.toAccountId);

      // Verify outbox event was created and can be relayed
      const pendingEventsBefore = await db
        .selectFrom('outbox')
        .selectAll()
        .where('status', '=', 'PENDING')
        .execute();

      expect(pendingEventsBefore.length).toBe(1);

      // Manually trigger the cron job logic
      await outboxRelayService.handleCron();

      const pendingEventsAfter = await db
        .selectFrom('outbox')
        .selectAll()
        .where('status', '=', 'PENDING')
        .execute();

      // Expect the event to have been processed
      expect(pendingEventsAfter.length).toBe(0);
    });

    it('should return 400 if Idempotency-Key is missing', async () => {
      const workspaceId = uuidv4();
      const payload = {
        amount: 50,
        fromAccountId: uuidv4(),
        toAccountId: uuidv4(),
      };

      await request(app.getHttpServer())
        .post('/transactions')
        .set('X-Workspace-Id', workspaceId)
        .send(payload)
        .expect(400);
    });

    it('should return the same transaction and not create a duplicate on idempotency key reuse', async () => {
      const workspaceId = uuidv4();
      const idempotencyKey = uuidv4();
      const payload = {
        amount: 200,
        note: 'Idempotent Transaction',
        fromAccountId: uuidv4(),
        toAccountId: uuidv4(),
      };

      const firstResponse = await request(app.getHttpServer())
        .post('/transactions')
        .set('X-Workspace-Id', workspaceId)
        .set('Idempotency-Key', idempotencyKey)
        .send(payload)
        .expect(201);

      const secondResponse = await request(app.getHttpServer())
        .post('/transactions')
        .set('X-Workspace-Id', workspaceId)
        .set('Idempotency-Key', idempotencyKey)
        .send(payload);

      // Status could be 200 or 201 depending on the exact implementation
      expect([200, 201]).toContain(secondResponse.status);
      expect(firstResponse.body.id).toBe(secondResponse.body.id);

      // Verify no duplicate transactions or outbox events in the DB
      const dbTransactions = await db
        .selectFrom('transaction')
        .selectAll()
        .execute();
      expect(dbTransactions.length).toBe(1);

      const dbOutbox = await db.selectFrom('outbox').selectAll().execute();
      expect(dbOutbox.length).toBe(1);
    });

    it('should return 400 if required payload fields are missing', async () => {
      const workspaceId = uuidv4();
      const idempotencyKey = uuidv4();
      const invalidPayload = {
        amount: 50,
        // missing fromAccountId and toAccountId
      };

      await request(app.getHttpServer())
        .post('/transactions')
        .set('X-Workspace-Id', workspaceId)
        .set('Idempotency-Key', idempotencyKey)
        .send(invalidPayload)
        .expect(400);
    });
  });

  describe('GET /transactions', () => {
    it('should return paginated transactions for workspace', async () => {
      const workspaceId = uuidv4();

      // Create one transaction
      await request(app.getHttpServer())
        .post('/transactions')
        .set('X-Workspace-Id', workspaceId)
        .set('Idempotency-Key', uuidv4())
        .send({
          amount: 150,
          note: 'Get All Test',
          fromAccountId: uuidv4(),
          toAccountId: uuidv4(),
        })
        .expect(201);

      const response = await request(app.getHttpServer())
        .get('/transactions')
        .set('X-Workspace-Id', workspaceId)
        .expect(200);

      const responseBody = response.body as TransactionPageResponseDto;

      expect(responseBody.content).toBeDefined();
      expect(Array.isArray(responseBody.content)).toBe(true);
      expect(responseBody.content.length).toBe(1);
    });
  });

  describe('GET /transactions/:id', () => {
    it('should return a transaction by id', async () => {
      const workspaceId = uuidv4();

      const createResponse = await request(app.getHttpServer())
        .post('/transactions')
        .set('X-Workspace-Id', workspaceId)
        .set('Idempotency-Key', uuidv4())
        .send({
          amount: 75,
          note: 'Find Me',
          fromAccountId: uuidv4(),
          toAccountId: uuidv4(),
        })
        .expect(201);

      const transactionId = (createResponse.body as TransactionResponseDto).id;

      const response = await request(app.getHttpServer())
        .get(`/transactions/${transactionId}`)
        .set('X-Workspace-Id', workspaceId)
        .expect(200);

      expect(response.body).toMatchObject({
        id: transactionId,
        amount: 75,
        note: 'Find Me',
      });
    });

    it('should return 404 if transaction does not exist', async () => {
      const workspaceId = uuidv4();
      const nonExistentId = uuidv4();

      await request(app.getHttpServer())
        .get(`/transactions/${nonExistentId}`)
        .set('X-Workspace-Id', workspaceId)
        .expect(404);
    });
  });
});
