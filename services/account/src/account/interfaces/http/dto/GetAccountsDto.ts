import { ApiPropertyOptional } from '@nestjs/swagger';
import {
  IsBoolean,
  IsInt,
  IsOptional,
  IsString,
  IsUUID,
  Min,
} from 'class-validator';
import { Transform, Type } from 'class-transformer';

export class GetAccountsDto {
  @ApiPropertyOptional({
    description: 'Include global accounts (workspaceId = null)',
    type: Boolean,
  })
  @IsOptional()
  @IsBoolean()
  @Type(() => Boolean)
  @Transform(({ value }) => value === 'true' || value === true)
  includeGlobal?: boolean;

  @ApiPropertyOptional({
    description: 'Page number (default: 0)',
    minimum: 0,
    default: 0,
  })
  @IsOptional()
  @IsInt()
  @Min(0)
  @Type(() => Number)
  page: number = 0;

  @ApiPropertyOptional({
    description: 'Page size (default: 10)',
    minimum: 1,
    default: 10,
  })
  @IsOptional()
  @IsInt()
  @Min(1)
  @Type(() => Number)
  size: number = 10;

  @ApiPropertyOptional({ description: 'Search term for account name' })
  @IsOptional()
  @IsString()
  searchTerm?: string;

  @ApiPropertyOptional({
    description: 'Filter by parent account UUID',
    format: 'uuid',
  })
  @IsOptional()
  @IsUUID()
  parentId?: string;
}
