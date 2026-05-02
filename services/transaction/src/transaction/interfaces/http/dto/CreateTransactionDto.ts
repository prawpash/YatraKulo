import {
  IsNotEmpty,
  IsNumber,
  IsOptional,
  IsString,
  IsUUID,
  Min,
} from 'class-validator';

export class CreateTransactionDto {
  @IsNumber()
  @Min(0)
  amount!: number;

  @IsOptional()
  @IsString()
  note?: string;

  @IsNotEmpty()
  @IsUUID()
  fromAccountId!: string;

  @IsNotEmpty()
  @IsUUID()
  toAccountId!: string;
}
