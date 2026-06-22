import { Injectable, NestMiddleware } from '@nestjs/common';
import { Request, Response, NextFunction } from 'express';
import { propagation, context, trace } from '@opentelemetry/api';
import { v4 as uuidv4 } from 'uuid';

@Injectable()
export class LoggingMiddleware implements NestMiddleware {
  private normalizeHeader(value: string | string[] | undefined): string | undefined {
    if (!value) return undefined;
    const str = Array.isArray(value) ? value[0] : value;
    const trimmed = str.trim();
    return trimmed && /^[^\r\n]+$/.test(trimmed) ? trimmed : undefined;
  }

  use(req: Request, res: Response, next: NextFunction) {
    const activeSpan = trace.getActiveSpan();
    
    const traceIdHeader = this.normalizeHeader(req.headers['x-trace-id']);
    const workspaceId = this.normalizeHeader(req.headers['x-workspace-id']);

    const traceId = activeSpan
      ? activeSpan.spanContext().traceId
      : traceIdHeader || uuidv4();

    res.setHeader('X-Trace-Id', traceId);

    let activeCtx = context.active();
    if (workspaceId) {
      let baggage =
        propagation.getBaggage(activeCtx) || propagation.createBaggage();
      baggage = baggage.setEntry('workspace.id', { value: workspaceId });
      activeCtx = propagation.setBaggage(activeCtx, baggage);
    }

    context.with(activeCtx, () => {
      next();
    });
  }
}
