import { Type } from 'class-transformer';
import { IsOptional, IsUUID, Min } from 'class-validator';

export class GetTransactionsDto {
  @IsOptional()
  @Type(() => Number)
  @Min(0)
  page = 0;

  @IsOptional()
  @Type(() => Number)
  @Min(1)
  size = 10;

  @IsOptional()
  @IsUUID()
  fromAccountId?: string;

  @IsOptional()
  @IsUUID()
  toAccountId?: string;
}
