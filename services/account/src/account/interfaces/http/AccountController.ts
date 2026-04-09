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
import {
  ApiTags,
  ApiBearerAuth,
  ApiOperation,
  ApiResponse,
  ApiHeader,
  ApiQuery,
} from '@nestjs/swagger';
import { CommandBus, QueryBus } from '@nestjs/cqrs';
import { GetAccountsDto } from './dto/GetAccountsDto';
import { CreateAccountDto } from './dto/CreateAccountDto';
import { UpdateAccountDto } from './dto/UpdateAccountDto';
import { AccountResponseDto } from './dto/AccountResponseDto';
import { AccountPageResponseDto } from './dto/AccountPageResponseDto';
import { XWorkspaceId } from './decorators/XWorkspaceId.decorator';
import { CurrentUser } from './decorators/CurrentUser.decorator';
import { GetAccountsQuery } from '@app/account/application/query/GetAccountsQuery';
import { GetAccountByIdQuery } from '@app/account/application/query/GetAccountByIdQuery';
import { CreateAccountCommand } from '@app/account/application/command/CreateAccountCommand';
import { UpdateAccountCommand } from '@app/account/application/command/UpdateAccountCommand';
import { Account } from '@app/account/domain/entity/Account';
import { DomainPage, createDomainPageRequest } from '@yk/shared';
import type { JwtPayload } from '@app/account/infrastructure/auth/JwtStrategy';
import { JwtAuthGuard } from '@app/account/infrastructure/auth/JwtAuthGuard';

@ApiTags('accounts')
@ApiBearerAuth()
@Controller('accounts')
@UseGuards(JwtAuthGuard)
export class AccountController {
  constructor(
    private readonly commandBus: CommandBus,
    private readonly queryBus: QueryBus,
  ) {}

  @Get('/')
  @ApiOperation({
    summary: 'Get all accounts for workspace',
    description:
      'Returns paginated list of accounts filtered by workspaceId. Use includeGlobal=true to also include accounts without workspaceId.',
  })
  @ApiHeader({
    name: 'X-Workspace-Id',
    required: true,
    description: 'Workspace UUID',
    schema: { format: 'uuid' },
  })
  @ApiQuery(() => GetAccountsDto)
  @ApiResponse({
    status: 200,
    description: 'Returns paginated accounts',
    type: AccountPageResponseDto,
  })
  @ApiResponse({
    status: 401,
    description: 'Unauthorized - missing or invalid JWT',
  })
  @ApiResponse({
    status: 403,
    description: 'Forbidden - user not member of workspace',
  })
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
  @ApiOperation({
    summary: 'Get account by ID',
    description:
      'Returns a single account by its UUID. Note: This endpoint is not workspace-scoped - it queries by account ID directly.',
  })
  @ApiResponse({
    status: 200,
    description: 'Returns the account',
    type: AccountResponseDto,
  })
  @ApiResponse({
    status: 401,
    description: 'Unauthorized - missing or invalid JWT',
  })
  @ApiResponse({ status: 404, description: 'Account not found' })
  async getAccountById(
    @Param('id', ParseUUIDPipe) id: string,
  ): Promise<Account> {
    return this.queryBus.execute<GetAccountByIdQuery, Account>(
      new GetAccountByIdQuery(id),
    );
  }

  @Post('/')
  @ApiOperation({
    summary: 'Create a new account',
    description: 'Creates a new account scoped to the specified workspace',
  })
  @ApiHeader({
    name: 'X-Workspace-Id',
    required: true,
    description: 'Workspace UUID',
    schema: { format: 'uuid' },
  })
  @ApiResponse({
    status: 201,
    description: 'Account created successfully',
    type: AccountResponseDto,
  })
  @ApiResponse({ status: 400, description: 'Bad request - validation error' })
  @ApiResponse({
    status: 401,
    description: 'Unauthorized - missing or invalid JWT',
  })
  @ApiResponse({
    status: 403,
    description: 'Forbidden - user not member of workspace',
  })
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
  @ApiOperation({
    summary: 'Update an existing account',
    description:
      'Updates account properties (name, description, type, parentId)',
  })
  @ApiResponse({ status: 200, description: 'Account updated successfully' })
  @ApiResponse({ status: 400, description: 'Bad request - validation error' })
  @ApiResponse({
    status: 401,
    description: 'Unauthorized - missing or invalid JWT',
  })
  @ApiResponse({
    status: 403,
    description: 'Forbidden - user not member of workspace',
  })
  @ApiResponse({ status: 404, description: 'Account not found' })
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
