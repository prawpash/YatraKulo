import { Injectable, NestMiddleware } from '@nestjs/common';
import { Request, Response, NextFunction } from 'express';
import { propagation, context, trace } from '@opentelemetry/api';
import { v4 as uuidv4 } from 'uuid';

@Injectable()
export class LoggingMiddleware implements NestMiddleware {
  use(req: Request, res: Response, next: NextFunction) {
    const activeSpan = trace.getActiveSpan();
    const traceId = activeSpan
      ? activeSpan.spanContext().traceId
      : (req.headers['x-trace-id'] as string) || uuidv4();
    const workspaceId = req.headers['x-workspace-id'] as string;

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
