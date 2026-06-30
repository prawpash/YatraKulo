export interface LogEvent<T extends string = string> {
  eventName?: T;
  message: string;
  durationMs?: number;
  metadata?: Record<string, any>;
  error?: Error;
}
