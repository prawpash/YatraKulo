import { IsBoolean, IsInt, IsOptional, IsString, IsUUID, Min } from 'class-validator';
import { Type } from 'class-transformer';

export class GetAccountsDto {
  @IsOptional()
  @IsBoolean()
  @Type(() => Boolean)
  includeGlobal?: boolean;

  @IsOptional()
  @IsInt()
  @Min(1)
  @Type(() => Number)
  page: number = 1;

  @IsOptional()
  @IsInt()
  @Min(1)
  @Type(() => Number)
  size: number = 10;

  @IsOptional()
  @IsString()
  searchTerm?: string;

  @IsOptional()
  @IsUUID()
  parentId?: string;
}
