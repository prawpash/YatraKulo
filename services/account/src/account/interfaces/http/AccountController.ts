import {
  BadRequestException,
  Body,
  Controller,
  Delete,
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
} from '@nestjs/swagger';
import { ApiStandardErrors } from '@app/shared/decorators/ApiStandardErrors.decorator';
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
import { DeleteAccountCommand } from '@app/account/application/command/DeleteAccountCommand';
import { Account } from '@app/account/domain/entity/Account';
import { DomainPage, createDomainPageRequest } from '@yk/shared';
import type { JwtPayload } from '@app/account/infrastructure/auth/JwtStrategy';
import { JwtAuthGuard } from '@app/account/infrastructure/auth/JwtAuthGuard';
import { PermissionsGuard } from '@app/account/infrastructure/auth/PermissionsGuard';
import {
  PERMISSIONS_CODE,
  RequirePermissions,
} from '@app/account/infrastructure/auth/RequirePermissions';

@ApiTags('accounts')
@ApiBearerAuth()
@Controller('accounts')
// @UseGuards(JwtAuthGuard, PermissionsGuard)
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
  @ApiResponse({
    status: 200,
    description: 'Returns paginated accounts',
    type: AccountPageResponseDto,
  })
  @ApiStandardErrors()
  @RequirePermissions(PERMISSIONS_CODE.ACCOUNT_READ)
  async getAccounts(
    @XWorkspaceId(ParseUUIDPipe) workspaceId: string,
    @Query() query: GetAccountsDto,
  ): Promise<AccountPageResponseDto> {
    const pageRequest = createDomainPageRequest(query.page, query.size);
    const result = await this.queryBus.execute<
      GetAccountsQuery,
      DomainPage<Account>
    >(
      new GetAccountsQuery(
        query.includeGlobal ?? false,
        pageRequest,
        workspaceId,
        query.searchTerm,
        query.parentId,
      ),
    );
    return AccountPageResponseDto.fromDomainPage(result);
  }

  @Get('/:id')
  @ApiOperation({
    summary: 'Get account by ID',
    description: 'Returns a single account by its UUID.',
  })
  @ApiHeader({
    name: 'X-Workspace-Id',
    required: true,
    description: 'Workspace UUID',
    schema: { format: 'uuid' },
  })
  @ApiResponse({
    status: 200,
    description: 'Returns the account',
    type: AccountResponseDto,
  })
  @ApiStandardErrors()
  @RequirePermissions(PERMISSIONS_CODE.ACCOUNT_READ)
  async getAccountById(
    @XWorkspaceId(ParseUUIDPipe) workspaceId: string,
    @Param(
      'id',
      new ParseUUIDPipe({
        exceptionFactory: () => {
          return new BadRequestException('Parameter `id` is not a valid UUID');
        },
      }),
    )
    id: string,
  ): Promise<AccountResponseDto> {
    const result = await this.queryBus.execute<GetAccountByIdQuery, Account>(
      new GetAccountByIdQuery(workspaceId, id),
    );
    return AccountResponseDto.fromDomain(result);
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
  @ApiStandardErrors()
  @RequirePermissions(PERMISSIONS_CODE.ACCOUNT_WRITE)
  async createAccount(
    @XWorkspaceId(ParseUUIDPipe) workspaceId: string,
    @CurrentUser() user: JwtPayload,
    @Body() dto: CreateAccountDto,
  ): Promise<AccountResponseDto> {
    const result = await this.commandBus.execute<CreateAccountCommand, Account>(
      new CreateAccountCommand(
        dto.name,
        dto.type,
        workspaceId,
        dto.parentId ?? null,
        dto.description ?? null,
        user.sub,
      ),
    );
    return AccountResponseDto.fromDomain(result);
  }

  @Patch('/:id')
  @ApiOperation({
    summary: 'Update an existing account',
    description:
      'Updates account properties (name, description, type, parentId)',
  })
  @ApiHeader({
    name: 'X-Workspace-Id',
    required: true,
    description: 'Workspace UUID',
    schema: { format: 'uuid' },
  })
  @ApiResponse({ status: 200, description: 'Account updated successfully' })
  @ApiStandardErrors()
  @RequirePermissions(PERMISSIONS_CODE.ACCOUNT_UPDATE)
  async updateAccount(
    @XWorkspaceId(ParseUUIDPipe) workspaceId: string,
    @Param(
      'id',
      new ParseUUIDPipe({
        exceptionFactory: () => {
          return new BadRequestException('Parameter `id` is not a valid UUID');
        },
      }),
    )
    id: string,
    @CurrentUser() user: JwtPayload,
    @Body() dto: UpdateAccountDto,
  ): Promise<void> {
    await this.commandBus.execute<UpdateAccountCommand, void>(
      new UpdateAccountCommand(
        workspaceId,
        id,
        user.sub,
        dto.name,
        dto.description,
        dto.parentId,
        dto.type,
      ),
    );
  }

  @Delete('/:id')
  @ApiOperation({
    summary: 'Delete an existing account',
    description: 'Soft-deletes an account by its UUID',
  })
  @ApiHeader({
    name: 'X-Workspace-Id',
    required: true,
    description: 'Workspace UUID',
    schema: { format: 'uuid' },
  })
  @ApiResponse({ status: 200, description: 'Account deleted successfully' })
  @ApiStandardErrors()
  @RequirePermissions(PERMISSIONS_CODE.ACCOUNT_DELETE)
  async deleteAccount(
    @XWorkspaceId(ParseUUIDPipe) workspaceId: string,
    @Param(
      'id',
      new ParseUUIDPipe({
        exceptionFactory: () => {
          return new BadRequestException('Parameter `id` is not a valid UUID');
        },
      }),
    )
    id: string,
    @CurrentUser() user: JwtPayload,
  ): Promise<void> {
    return this.commandBus.execute<DeleteAccountCommand, void>(
      new DeleteAccountCommand(workspaceId, id, user.sub),
    );
  }
}
