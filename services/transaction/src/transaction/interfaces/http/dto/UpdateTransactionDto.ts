import { IsNumber, IsOptional, IsString, Min } from 'class-validator';

export class UpdateTransactionDto {
  @IsOptional()
  @IsNumber()
  @Min(0)
  amount?: number;

  @IsOptional()
  @IsString()
  note?: string | null;
}
