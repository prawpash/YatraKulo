import { OmitType, PartialType } from '@nestjs/swagger';
import { IsOptional, IsUUID, ValidateIf } from 'class-validator';
import { CreateAccountDto } from './CreateAccountDto';

export class UpdateAccountDto extends PartialType(
  OmitType(CreateAccountDto, ['parentId'] as const),
) {
  @IsOptional()
  @ValidateIf((o) => o.parentId !== null)
  @IsUUID()
  parentId?: string | null;
}
