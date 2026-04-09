import {
  IsEnum,
  IsNotEmpty,
  IsOptional,
  IsString,
  IsUUID,
} from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { AccountType } from '@app/account/domain/enums/AccountType';

export class CreateAccountDto {
  @ApiProperty({ description: 'Account name', maxLength: 100 })
  @IsNotEmpty()
  @IsString()
  name!: string;

  @ApiProperty({ description: 'Account type', enum: AccountType })
  @IsNotEmpty()
  @IsEnum(AccountType)
  type!: AccountType;

  @ApiPropertyOptional({ description: 'Parent account UUID', format: 'uuid' })
  @IsOptional()
  @IsUUID()
  parentId?: string;

  @ApiPropertyOptional({ description: 'Account description' })
  @IsOptional()
  @IsString()
  description?: string;
}
