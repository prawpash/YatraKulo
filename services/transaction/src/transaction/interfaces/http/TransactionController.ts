import {
  BadRequestException,
  Body,
  Controller,
  Delete,
  Get,
  Headers,
  Param,
  ParseUUIDPipe,
  Patch,
  Post,
  Query,
  UseGuards,
} from '@nestjs/common';
import {
  ApiBearerAuth,
  ApiHeader,
  ApiOperation,
  ApiResponse,
  ApiTags,
} from '@nestjs/swagger';
import { ApiStandardErrors } from '@app/shared/decorators/ApiStandardErrors.decorator';
import { CommandBus, QueryBus } from '@nestjs/cqrs';
import { DomainPage, createDomainPageRequest } from '@yk/shared';
import { Transaction } from '@app/transaction/domain/entity/Transaction';
import type { JwtPayload } from '@app/transaction/infrastructure/auth/JwtStrategy';
import { JwtAuthGuard } from '@app/transaction/infrastructure/auth/JwtAuthGuard';
import { PermissionsGuard } from '@app/transaction/infrastructure/auth/PermissionsGuard';
import {
  PERMISSIONS_CODE,
  RequirePermissions,
} from '@app/transaction/infrastructure/auth/RequirePermissions';
import { CurrentUser } from './decorators/CurrentUser.decorator';
import { XWorkspaceId } from './decorators/XWorkspaceId.decorator';
import { GetTransactionsDto } from './dto/GetTransactionsDto';
import { CreateTransactionDto } from './dto/CreateTransactionDto';
import { UpdateTransactionDto } from './dto/UpdateTransactionDto';
import { TransactionResponseDto } from './dto/TransactionResponseDto';
import { TransactionPageResponseDto } from './dto/TransactionPageResponseDto';
import { GetTransactionsQuery } from '@app/transaction/application/query/GetTransactionsQuery';
import { GetTransactionByIdQuery } from '@app/transaction/application/query/GetTransactionByIdQuery';
import { CreateTransactionCommand } from '@app/transaction/application/command/CreateTransactionCommand';
import { UpdateTransactionCommand } from '@app/transaction/application/command/UpdateTransactionCommand';
import { DeleteTransactionCommand } from '@app/transaction/application/command/DeleteTransactionCommand';

@ApiTags('transactions')
@ApiBearerAuth()
@Controller('transactions')
@UseGuards(JwtAuthGuard, PermissionsGuard)
export class TransactionController {
  constructor(
    private readonly commandBus: CommandBus,
    private readonly queryBus: QueryBus,
  ) {}

  @Get('/')
  @ApiOperation({ summary: 'Get all transactions for workspace' })
  @ApiHeader({
    name: 'X-Workspace-Id',
    required: true,
    description: 'Workspace UUID',
    schema: { format: 'uuid' },
  })
  @ApiResponse({
    status: 200,
    description: 'Returns paginated transactions',
    type: TransactionPageResponseDto,
  })
  @ApiStandardErrors()
  @RequirePermissions(PERMISSIONS_CODE.TRANSACTION_READ)
  async getTransactions(
    @XWorkspaceId(ParseUUIDPipe) workspaceId: string,
    @Query() query: GetTransactionsDto,
  ): Promise<TransactionPageResponseDto> {
    const pageRequest = createDomainPageRequest(query.page, query.size);
    const result = await this.queryBus.execute<
      GetTransactionsQuery,
      DomainPage<Transaction>
    >(
      new GetTransactionsQuery(
        workspaceId,
        pageRequest,
        query.fromAccountId,
        query.toAccountId,
      ),
    );

    return TransactionPageResponseDto.fromDomainPage(result);
  }

  @Get('/:id')
  @ApiOperation({ summary: 'Get transaction by ID' })
  @ApiHeader({
    name: 'X-Workspace-Id',
    required: true,
    description: 'Workspace UUID',
    schema: { format: 'uuid' },
  })
  @ApiResponse({
    status: 200,
    description: 'Returns the transaction',
    type: TransactionResponseDto,
  })
  @ApiStandardErrors()
  @RequirePermissions(PERMISSIONS_CODE.TRANSACTION_READ)
  async getTransactionById(
    @XWorkspaceId(ParseUUIDPipe) workspaceId: string,
    @Param(
      'id',
      new ParseUUIDPipe({
        exceptionFactory: () =>
          new BadRequestException('Parameter `id` is not a valid UUID'),
      }),
    )
    id: string,
  ): Promise<TransactionResponseDto> {
    const result = await this.queryBus.execute<
      GetTransactionByIdQuery,
      Transaction
    >(new GetTransactionByIdQuery(workspaceId, id));

    return TransactionResponseDto.fromDomain(result);
  }

  @Post('/')
  @ApiOperation({ summary: 'Create transaction' })
  @ApiHeader({
    name: 'X-Workspace-Id',
    required: true,
    description: 'Workspace UUID',
    schema: { format: 'uuid' },
  })
  @ApiHeader({
    name: 'Idempotency-Key',
    required: true,
    description: 'Unique key to ensure idempotent transaction creation',
  })
  @ApiResponse({
    status: 201,
    description: 'Transaction created successfully',
    type: TransactionResponseDto,
  })
  @ApiStandardErrors()
  @RequirePermissions(PERMISSIONS_CODE.TRANSACTION_WRITE)
  async createTransaction(
    @XWorkspaceId(ParseUUIDPipe) workspaceId: string,
    @CurrentUser() user: JwtPayload,
    @Headers('idempotency-key') idempotencyKey: string,
    @Body() dto: CreateTransactionDto,
  ): Promise<TransactionResponseDto> {
    if (!idempotencyKey) {
      throw new BadRequestException('Missing required Idempotency-Key header');
    }

    const result = await this.commandBus.execute<
      CreateTransactionCommand,
      Transaction
    >(
      new CreateTransactionCommand(
        workspaceId,
        dto.amount,
        dto.note ?? null,
        dto.fromAccountId,
        dto.toAccountId,
        idempotencyKey,
        user.sub,
      ),
    );

    return TransactionResponseDto.fromDomain(result);
  }

  @Patch('/:id')
  @ApiOperation({ summary: 'Update transaction' })
  @ApiHeader({
    name: 'X-Workspace-Id',
    required: true,
    description: 'Workspace UUID',
    schema: { format: 'uuid' },
  })
  @ApiResponse({ status: 200, description: 'Transaction updated successfully' })
  @ApiStandardErrors()
  @RequirePermissions(PERMISSIONS_CODE.TRANSACTION_UPDATE)
  async updateTransaction(
    @XWorkspaceId(ParseUUIDPipe) workspaceId: string,
    @Param(
      'id',
      new ParseUUIDPipe({
        exceptionFactory: () =>
          new BadRequestException('Parameter `id` is not a valid UUID'),
      }),
    )
    id: string,
    @CurrentUser() user: JwtPayload,
    @Body() dto: UpdateTransactionDto,
  ): Promise<void> {
    await this.commandBus.execute<UpdateTransactionCommand, void>(
      new UpdateTransactionCommand(
        workspaceId,
        id,
        user.sub,
        dto.amount,
        dto.note,
      ),
    );
  }

  @Delete('/:id')
  @ApiOperation({ summary: 'Delete transaction' })
  @ApiHeader({
    name: 'X-Workspace-Id',
    required: true,
    description: 'Workspace UUID',
    schema: { format: 'uuid' },
  })
  @ApiResponse({ status: 200, description: 'Transaction deleted successfully' })
  @ApiStandardErrors()
  @RequirePermissions(PERMISSIONS_CODE.TRANSACTION_DELETE)
  async deleteTransaction(
    @XWorkspaceId(ParseUUIDPipe) workspaceId: string,
    @Param(
      'id',
      new ParseUUIDPipe({
        exceptionFactory: () =>
          new BadRequestException('Parameter `id` is not a valid UUID'),
      }),
    )
    id: string,
    @CurrentUser() user: JwtPayload,
  ): Promise<void> {
    await this.commandBus.execute<DeleteTransactionCommand, void>(
      new DeleteTransactionCommand(workspaceId, id, user.sub),
    );
  }
}
