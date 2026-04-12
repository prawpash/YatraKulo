import {
  createParamDecorator,
  ExecutionContext,
  UnauthorizedException,
} from '@nestjs/common';
import { isUUID } from 'class-validator';

export const XWorkspaceId = createParamDecorator(
  (data: unknown, ctx: ExecutionContext) => {
    const request: Request = ctx.switchToHttp().getRequest();

    const workspaceId = request.headers['x-workspace-id'] as string;

    if (!isUUID(workspaceId)) {
      throw new UnauthorizedException(
        `Malformed workspace ID found in request headers`,
      );
    }

    return workspaceId;
  },
);
