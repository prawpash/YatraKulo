import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { AccountType } from '@app/account/domain/enums/AccountType';

export class AccountResponseDto {
  @ApiProperty({ description: 'Account UUID', format: 'uuid' })
  id!: string;

  @ApiProperty({ description: 'Account name', maxLength: 100 })
  name!: string;

  @ApiPropertyOptional({
    description: 'Account description',
    format: 'string',
    nullable: true,
  })
  description!: string | null;

  @ApiProperty({ description: 'Account type', enum: AccountType })
  type!: AccountType;

  @ApiPropertyOptional({
    description: 'Workspace UUID (null for global accounts)',
    format: 'uuid',
    nullable: true,
  })
  workspaceId!: string | null;

  @ApiPropertyOptional({
    description: 'Parent account UUID',
    format: 'uuid',
    nullable: true,
  })
  parentId!: string | null;

  @ApiProperty({ description: 'Creation timestamp', format: 'date-time' })
  createdAt!: Date;

  @ApiProperty({ description: 'Last update timestamp', format: 'date-time' })
  updatedAt!: Date;

  @ApiPropertyOptional({
    description: 'Deletion timestamp',
    format: 'date-time',
    nullable: true,
  })
  deletedAt!: Date | null;

  @ApiPropertyOptional({
    description: 'Creator user UUID',
    format: 'uuid',
    nullable: true,
  })
  createdBy!: string | null;

  @ApiPropertyOptional({
    description: 'Last updater user UUID',
    format: 'uuid',
    nullable: true,
  })
  updatedBy!: string | null;

  @ApiPropertyOptional({
    description: 'Deleter user UUID',
    format: 'uuid',
    nullable: true,
  })
  deletedBy!: string | null;
}
