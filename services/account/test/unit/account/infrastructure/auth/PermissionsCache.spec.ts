import { PermissionsCache } from '@app/account/infrastructure/auth/PermissionsCache';

describe('PermissionsCache', () => {
  let cache: PermissionsCache;

  beforeEach(() => {
    cache = new PermissionsCache();
  });

  it('should set and get values', () => {
    const key = 'key-1';
    const val = ['read'];
    cache.set(key, val);
    expect(cache.get(key)).toEqual(val);
  });

  it('should return undefined for non-existent key', () => {
    expect(cache.get('missing')).toBeUndefined();
  });

  it('should delete value', () => {
    const key = 'key-1';
    cache.set(key, ['read']);
    cache.delete(key);
    expect(cache.get(key)).toBeUndefined();
  });

  it('should flush all values', () => {
    cache.set('k1', ['v1']);
    cache.set('k2', ['v2']);
    cache.flush();
    expect(cache.get('k1')).toBeUndefined();
    expect(cache.get('k2')).toBeUndefined();
  });
});
