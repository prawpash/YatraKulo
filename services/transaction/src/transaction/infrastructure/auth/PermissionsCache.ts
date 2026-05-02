import { Injectable, Logger } from '@nestjs/common';
import NodeCache from 'node-cache';

@Injectable()
export class PermissionsCache {
  private readonly logger = new Logger(PermissionsCache.name);
  private readonly cache: NodeCache;

  constructor() {
    this.cache = new NodeCache({
      stdTTL: 180, // 3 minutes
      checkperiod: 60,
    });
  }

  get(key: string): string[] | undefined {
    const value = this.cache.get<string[]>(key);
    if (value) {
      this.logger.debug(`Cache HIT for key: ${key}`);
    } else {
      this.logger.debug(`Cache MISS for key: ${key}`);
    }
    return value;
  }

  set(key: string, value: string[]): boolean {
    const result = this.cache.set(key, value);
    if (result) {
      this.logger.debug(`Cache SET for key: ${key}`);
    }
    return result;
  }

  delete(key: string): number {
    const result = this.cache.del(key);
    if (result > 0) {
      this.logger.debug(`Cache DELETE for key: ${key}`);
    }
    return result;
  }

  flush(): void {
    this.cache.flushAll();
    this.logger.log('Cache flushed');
  }
}
