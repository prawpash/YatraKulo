import { SetMetadata } from '@nestjs/common';

export const PERMISSIONS_KEY = 'permissions';

export const PERMISSIONS_CODE = {
  ACCOUNT_READ: 'account.read',
  ACCOUNT_CREATE: 'account.create',
  ACCOUNT_UPDATE: 'account.update',
  ACCOUNT_DELETE: 'account.delete',
} as const;

export type PERMISSIONS_CODE =
  (typeof PERMISSIONS_CODE)[keyof typeof PERMISSIONS_CODE];

export const RequirePermissions = (...permissions: PERMISSIONS_CODE[]) =>
  SetMetadata(PERMISSIONS_KEY, permissions);
