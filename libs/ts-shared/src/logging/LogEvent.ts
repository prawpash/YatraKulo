import { LogEventName } from './LogEventName';

export interface LogEvent {
  eventName?: LogEventName;
  message: string;
  durationMs?: number;
  metadata?: Record<string, any>;
  error?: Error;
}
