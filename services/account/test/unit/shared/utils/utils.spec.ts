import { parseNumber } from '@app/shared/utils/utils';
import { ValidationException } from '@yk/shared';

describe('utils', () => {
  describe('parseNumber', () => {
    it('should parse valid number string', () => {
      expect(parseNumber('10', 0, 'key')).toBe(10);
    });

    it('should return fallback if value is undefined', () => {
      expect(parseNumber(undefined, 5, 'key')).toBe(5);
    });

    it('should throw ValidationException for invalid string', () => {
      expect(() => parseNumber('abc', 0, 'key')).toThrow(ValidationException);
    });
  });
});
