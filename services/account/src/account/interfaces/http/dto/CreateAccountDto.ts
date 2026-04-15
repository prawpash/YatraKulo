import {
  IsEnum,
  IsNotEmpty,
  IsOptional,
  IsString,
  IsUUID,
  MaxLength,
  MinLength,
} from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { AccountType } from '@app/account/domain/enums/AccountType';
import { IsNotBlank } from '@app/shared/decorators/validation/IsNotBlank';

export class CreateAccountDto {
  @ApiProperty({ description: 'Account name', maxLength: 100 })
  @IsNotEmpty()
  @IsNotBlank()
  @IsString()
  @MinLength(3)
  @MaxLength(100)
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
