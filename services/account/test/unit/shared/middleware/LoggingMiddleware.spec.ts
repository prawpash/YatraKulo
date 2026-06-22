import { LoggingMiddleware } from '@app/shared/middleware/LoggingMiddleware';
import { AppLogger } from '@yk/shared';
import { Request, Response } from 'express';
import { propagation, context } from '@opentelemetry/api';
import { AsyncLocalStorageContextManager } from '@opentelemetry/context-async-hooks';

describe('LoggingMiddleware & AppLogger integration', () => {
  let middleware: LoggingMiddleware;

  beforeAll(() => {
    const contextManager = new AsyncLocalStorageContextManager();
    contextManager.enable();
    context.setGlobalContextManager(contextManager);
  });

  afterAll(() => {
    context.disable();
  });

  beforeEach(() => {
    middleware = new LoggingMiddleware();
  });

  it('should run next() in a context populated with workspaceId in OTel baggage', (done) => {
    const req = {
      headers: {
        'x-trace-id': 'test-trace-id',
        'x-workspace-id': 'test-workspace-id',
      },
    } as unknown as Request;

    const res = {
      setHeader: jest.fn(),
    } as unknown as Response;

    const next = jest.fn(() => {
      const activeCtx = context.active();
      const baggage = propagation.getBaggage(activeCtx);
      expect(baggage).toBeDefined();
      expect(baggage?.getEntry('workspace.id')?.value).toBe('test-workspace-id');
      done();
    });

    middleware.use(req, res, next);
    expect(res.setHeader).toHaveBeenCalledWith('X-Trace-Id', 'test-trace-id');
  });

  it('should generate a traceId if not provided', (done) => {
    const req = {
      headers: {},
    } as unknown as Request;

    const res = {
      setHeader: jest.fn(),
    } as unknown as Response;

    const next = jest.fn(() => {
      done();
    });

    middleware.use(req, res, next);
    expect(res.setHeader).toHaveBeenCalledWith('X-Trace-Id', expect.any(String));
  });

  it('should allow AppLogger to output logs containing workspaceId from OTel context', (done) => {
    const logger = new AppLogger('TestContext');
    const emitSpy = jest.spyOn(logger as any, 'emit');

    const req = {
      headers: {
        'x-trace-id': 'custom-trace-123',
        'x-workspace-id': 'workspace-456',
      },
    } as unknown as Request;

    const res = {
      setHeader: jest.fn(),
    } as unknown as Response;

    const next = jest.fn(() => {
      logger.info('Hello world');
      
      expect(emitSpy).toHaveBeenCalledWith(
        'info',
        'Hello world',
        undefined
      );
      
      emitSpy.mockRestore();
      done();
    });

    middleware.use(req, res, next);
  });
});
