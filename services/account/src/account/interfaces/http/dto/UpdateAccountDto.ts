import { ApiPropertyOptional, OmitType, PartialType } from '@nestjs/swagger';
import { IsOptional, IsUUID, ValidateIf } from 'class-validator';
import { CreateAccountDto } from './CreateAccountDto';

export class UpdateAccountDto extends PartialType(
  OmitType(CreateAccountDto, ['parentId'] as const),
) {
  @ApiPropertyOptional({
    description: 'Parent account UUID (set to null to remove parent)',
    format: 'uuid',
    nullable: true,
  })
  @IsOptional()
  @ValidateIf((o: { parentId: string | null }) => o.parentId !== null)
  @IsUUID()
  parentId?: string | null;
}
