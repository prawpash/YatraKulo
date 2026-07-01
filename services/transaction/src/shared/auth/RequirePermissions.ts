import { SetMetadata } from '@nestjs/common';

export const PERMISSIONS_KEY = 'permissions';

export const PERMISSIONS_CODE = {
  TRANSACTION_READ: 'transaction.read',
  TRANSACTION_WRITE: 'transaction.write',
  TRANSACTION_UPDATE: 'transaction.update',
  TRANSACTION_DELETE: 'transaction.delete',
  TRANSACTION_IMPORT: 'transaction.import',
} as const;

export type PERMISSIONS_CODE =
  (typeof PERMISSIONS_CODE)[keyof typeof PERMISSIONS_CODE];

export const RequirePermissions = (...permissions: PERMISSIONS_CODE[]) =>
  SetMetadata(PERMISSIONS_KEY, permissions);
