import { ApiProperty } from '@nestjs/swagger';
import { Transaction } from '@app/transaction/domain/entity/Transaction';

export class TransactionResponseDto {
  @ApiProperty()
  id!: string;

  @ApiProperty()
  amount!: number;

  @ApiProperty({ nullable: true })
  note!: string | null;

  @ApiProperty()
  fromAccountId!: string;

  @ApiProperty()
  toAccountId!: string;

  @ApiProperty()
  idempotencyKey!: string;

  @ApiProperty()
  workspaceId!: string;

  @ApiProperty()
  createdAt!: Date;

  @ApiProperty()
  updatedAt!: Date;

  static fromDomain(transaction: Transaction): TransactionResponseDto {
    return {
      id: transaction.id,
      amount: transaction.amount,
      note: transaction.note,
      fromAccountId: transaction.fromAccountId,
      toAccountId: transaction.toAccountId,
      idempotencyKey: transaction.idempotencyKey,
      workspaceId: transaction.workspaceId,
      createdAt: transaction.createdAt,
      updatedAt: transaction.updatedAt,
    };
  }
}
