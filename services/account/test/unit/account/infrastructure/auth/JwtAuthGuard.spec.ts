import { JwtAuthGuard } from '@app/shared/auth/JwtAuthGuard';
import { UnauthorizedException } from '@nestjs/common';

describe('JwtAuthGuard', () => {
  let guard: JwtAuthGuard;

  beforeEach(() => {
    guard = new JwtAuthGuard();
  });

  it('should return user if no error and user exists', () => {
    const user = { id: 1 };
    expect(guard.handleRequest(null, user)).toBe(user);
  });

  it('should throw UnauthorizedException if error occurs', () => {
    expect(() => {
      guard.handleRequest(new Error('fail'), null);
    }).toThrow(UnauthorizedException);
  });

  it('should throw UnauthorizedException if user is missing', () => {
    expect(() => {
      guard.handleRequest(null, null);
    }).toThrow(UnauthorizedException);
  });
});
