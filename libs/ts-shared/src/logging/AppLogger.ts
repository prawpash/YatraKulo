import pino, { Logger } from 'pino';
import { LogEvent } from './LogEvent';
import { propagation, context as otelContext } from '@opentelemetry/api';

export class AppLogger {
  private logger: Logger;

  constructor(context?: string) {
    const useJson =
      process.env.NODE_ENV === 'production' ||
      process.env.LOG_FORMAT === 'json';

    this.logger = pino({
      level: process.env.LOG_LEVEL || 'info',
      transport: !useJson
        ? { target: 'pino-pretty', options: { colorize: true } }
        : undefined,
      base: { context },
      serializers: { error: pino.stdSerializers.err },
    });
  }

  log(event: LogEvent | string, context?: string) {
    this.emit('info', event, context);
  }
  info(event: LogEvent | string, context?: string) {
    this.emit('info', event, context);
  }
  warn(event: LogEvent | string, context?: string) {
    this.emit('warn', event, context);
  }
  debug(event: LogEvent | string, context?: string) {
    this.emit('debug', event, context);
  }
  error(event: LogEvent | string, trace?: string, context?: string) {
    this.emit('error', event, context, trace);
  }

  private emit(
    level: pino.Level,
    event: LogEvent | string,
    context?: string,
    trace?: string,
  ) {
    const activeContext = otelContext.active();
    const baggage = propagation.getBaggage(activeContext);
    const workspaceId = baggage?.getEntry('workspace.id')?.value;

    const ctxValues: Record<string, any> = {};
    if (workspaceId) {
      ctxValues['workspace.id'] = workspaceId;
    }

    let logObj: Record<string, any>;
    let msg: string;
    
    if (typeof event === 'string') {
      logObj = { context, trace, ...ctxValues };
      msg = event;
    } else {
      const { message, ...rest } = event;
      logObj = { ...rest, context, trace, ...ctxValues };
      msg = message;
    }

    this.logger[level](logObj, msg);
  }
}
