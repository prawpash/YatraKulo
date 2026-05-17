import { JwtPayload } from '@app/transaction/infrastructure/auth/JwtStrategy';
import { createParamDecorator, ExecutionContext } from '@nestjs/common';
import { Request } from 'express';

export const CurrentUser = createParamDecorator(
  (data: unknown, ctx: ExecutionContext) => {
    const request: Request = ctx.switchToHttp().getRequest();

    return request.user as JwtPayload;
  },
);
