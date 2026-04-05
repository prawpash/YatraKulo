import {
  Body,
  Controller,
  Get,
  Param,
  ParseUUIDPipe,
  Patch,
  Post,
  Query,
  UseGuards,
} from '@nestjs/common';
import { CommandBus, QueryBus } from '@nestjs/cqrs';
import { JwtAuthGuard } from '../../../account/infrastructure/auth/JwtAuthGuard';
import { GetAccountsDto } from './dto/GetAccountsDto';
import { CreateAccountDto } from './dto/CreateAccountDto';
import { UpdateAccountDto } from './dto/UpdateAccountDto';
import { XWorkspaceId } from './decorators/XWorkspaceId.decorator';
import { CurrentUser } from './decorators/CurrentUser.decorator';
import { GetAccountsQuery } from '@app/account/application/query/GetAccountsQuery';
import { GetAccountByIdQuery } from '@app/account/application/query/GetAccountByIdQuery';
import { CreateAccountCommand } from '@app/account/application/command/CreateAccountCommand';
import { UpdateAccountCommand } from '@app/account/application/command/UpdateAccountCommand';
import { Account } from '@app/account/domain/entity/Account';
import { DomainPage, createDomainPageRequest } from '@yk/shared';
import type { JwtPayload } from '@app/account/infrastructure/auth/JwtStrategy';

@Controller('accounts')
@UseGuards(JwtAuthGuard)
export class AccountController {
  constructor(
    private readonly commandBus: CommandBus,
    private readonly queryBus: QueryBus,
  ) {}

  @Get('/')
  async getAccounts(
    @XWorkspaceId(ParseUUIDPipe) workspaceId: string,
    @Query() query: GetAccountsDto,
  ): Promise<DomainPage<Account>> {
    const pageRequest = createDomainPageRequest(query.page, query.size);
    return this.queryBus.execute<GetAccountsQuery, DomainPage<Account>>(
      new GetAccountsQuery(
        query.includeGlobal ?? false,
        pageRequest,
        workspaceId,
        query.searchTerm,
        query.parentId,
      ),
    );
  }

  @Get('/:id')
  async getAccountById(
    @Param('id', ParseUUIDPipe) id: string,
  ): Promise<Account> {
    return this.queryBus.execute<GetAccountByIdQuery, Account>(
      new GetAccountByIdQuery(id),
    );
  }

  @Post('/')
  async createAccount(
    @XWorkspaceId(ParseUUIDPipe) workspaceId: string,
    @CurrentUser() user: JwtPayload,
    @Body() dto: CreateAccountDto,
  ): Promise<Account> {
    return this.commandBus.execute<CreateAccountCommand, Account>(
      new CreateAccountCommand(
        dto.name,
        dto.type,
        workspaceId,
        dto.parentId ?? null,
        dto.description ?? null,
        user.sub,
      ),
    );
  }

  @Patch('/:id')
  async updateAccount(
    @Param('id', ParseUUIDPipe) id: string,
    @CurrentUser() user: JwtPayload,
    @Body() dto: UpdateAccountDto,
  ): Promise<void> {
    return this.commandBus.execute<UpdateAccountCommand, void>(
      new UpdateAccountCommand(
        id,
        user.sub,
        dto.name,
        dto.description,
        dto.parentId,
        dto.type,
      ),
    );
  }
}
