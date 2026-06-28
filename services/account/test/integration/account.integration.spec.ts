import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import request from 'supertest';
import { AppModule } from '../../src/app.module';
import { TestcontainersSetup } from '../utils/TestcontainersSetup';
import { JwtAuthGuard } from '../../src/account/infrastructure/auth/JwtAuthGuard';
import { PermissionsGuard } from '../../src/account/infrastructure/auth/PermissionsGuard';
import {
  MockJwtAuthGuard,
  MockPermissionsGuard,
} from '../utils/MockAuthGuards';
import { v4 as uuidv4 } from 'uuid';
import { JwtStrategy } from '@app/account/infrastructure/auth/JwtStrategy';
import { AccountPageResponseDto } from '@app/account/interfaces/http/dto/AccountPageResponseDto';
import { AccountResponseDto } from '@app/account/interfaces/http/dto/AccountResponseDto';
import { GlobalExceptionFilter } from '../../src/GlobalExceptionFilter';

describe('AccountController (Integration)', () => {
  let app: INestApplication;
  const BASE_PATH = '/api/v1/accounts';

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
    app.setGlobalPrefix('api/v1');
    app.useGlobalPipes(
      new ValidationPipe({ transform: true, whitelist: true }),
    );
    app.useGlobalFilters(new GlobalExceptionFilter());
    await app.init();
  }, 120000); // 2 minutes timeout for container pull and startup

  afterAll(async () => {
    if (app) {
      await app.close();
    }
    await TestcontainersSetup.stop();
  });

  describe('POST /api/v1/accounts', () => {
    it('should create a new account', async () => {
      const workspaceId = uuidv4();
      const payload = {
        name: 'Test Integration Account',
        type: 'ASSET',
        description: 'Account created in integration test',
      };

      const response = await request(app.getHttpServer())
        .post(BASE_PATH)
        .set('X-Workspace-Id', workspaceId)
        .send(payload)
        .expect(201);

      const responseBody = response.body as AccountResponseDto;

      expect(response.body).toMatchObject({
        name: payload.name,
        type: payload.type,
      });
      expect(responseBody).toBeDefined();
    });

    it('should return 400 if name is missing', async () => {
      const workspaceId = uuidv4();
      const payload = {
        type: 'ASSET',
      };

      await request(app.getHttpServer())
        .post(BASE_PATH)
        .set('X-Workspace-Id', workspaceId)
        .send(payload)
        .expect(400);
    });
  });

  describe('GET /api/v1/accounts', () => {
    it('should return paginated accounts for workspace', async () => {
      const workspaceId = uuidv4();

      // First create one
      await request(app.getHttpServer())
        .post(BASE_PATH)
        .set('X-Workspace-Id', workspaceId)
        .send({ name: 'Account 1', type: 'ASSET' })
        .expect(201);

      const response = await request(app.getHttpServer())
        .get(BASE_PATH)
        .set('X-Workspace-Id', workspaceId)
        .expect(200);

      const responseBody = response.body as AccountPageResponseDto;

      expect(responseBody.content).toBeDefined();
      expect(Array.isArray(responseBody.content)).toBe(true);

      // Filter by workspace just in case there's noise,
      // although Testcontainers should be fresh.
      const workspaceAccounts = responseBody.content.filter(
        (a) => a.name === 'Account 1',
      );
      expect(workspaceAccounts.length).toBeGreaterThanOrEqual(1);
    });
  });

  describe('GET /api/v1/accounts/:id', () => {
    it('should return an account by id', async () => {
      const workspaceId = uuidv4();

      // 1. Create an account
      const createResponse = await request(app.getHttpServer())
        .post(BASE_PATH)
        .set('X-Workspace-Id', workspaceId)
        .send({ name: 'Find Me', type: 'ASSET' })
        .expect(201);

      const accountId = (createResponse.body as AccountResponseDto).id;

      // 2. Fetch it
      const response = await request(app.getHttpServer())
        .get(`${BASE_PATH}/${accountId}`)
        .set('X-Workspace-Id', workspaceId)
        .expect(200);

      expect(response.body).toMatchObject({
        id: accountId,
        name: 'Find Me',
        type: 'ASSET',
      });
    });

    it('should return 404 if account does not exist', async () => {
      const workspaceId = uuidv4();
      const nonExistentId = uuidv4();

      await request(app.getHttpServer())
        .get(`${BASE_PATH}/${nonExistentId}`)
        .set('X-Workspace-Id', workspaceId)
        .expect(404);
    });

    it('should return 400 if id is not a valid UUID', async () => {
      const workspaceId = uuidv4();

      await request(app.getHttpServer())
        .get(`${BASE_PATH}/not-a-uuid`)
        .set('X-Workspace-Id', workspaceId)
        .expect(400);
    });
  });

  describe('PATCH /api/v1/accounts/:id', () => {
    it('should update an existing account', async () => {
      const workspaceId = uuidv4();

      // 1. Create an account
      const createResponse = await request(app.getHttpServer())
        .post(BASE_PATH)
        .set('X-Workspace-Id', workspaceId)
        .send({ name: 'Old Name', type: 'ASSET' })
        .expect(201);

      const accountId = (createResponse.body as AccountResponseDto).id;

      // 2. Update it
      const updatePayload = {
        name: 'New Name',
        description: 'Updated description',
      };

      await request(app.getHttpServer())
        .patch(`${BASE_PATH}/${accountId}`)
        .set('X-Workspace-Id', workspaceId)
        .send(updatePayload)
        .expect(200);

      // 3. Verify the update
      const getResponse = await request(app.getHttpServer())
        .get(`${BASE_PATH}/${accountId}`)
        .set('X-Workspace-Id', workspaceId)
        .expect(200);

      expect(getResponse.body).toMatchObject({
        id: accountId,
        name: 'New Name',
        description: 'Updated description',
      });
    });
  });

  describe('DELETE /api/v1/accounts/:id', () => {
    it('should delete an existing account', async () => {
      const workspaceId = uuidv4();

      // 1. Create an account
      const createResponse = await request(app.getHttpServer())
        .post(BASE_PATH)
        .set('X-Workspace-Id', workspaceId)
        .send({ name: 'To Be Deleted', type: 'ASSET' })
        .expect(201);

      const accountId = (createResponse.body as AccountResponseDto).id;

      // 2. Delete it
      await request(app.getHttpServer())
        .delete(`${BASE_PATH}/${accountId}`)
        .set('X-Workspace-Id', workspaceId)
        .expect(200);

      // 3. Verify it's gone (returns 404)
      await request(app.getHttpServer())
        .get(`${BASE_PATH}/${accountId}`)
        .set('X-Workspace-Id', workspaceId)
        .expect(404);
    });
  });
});
