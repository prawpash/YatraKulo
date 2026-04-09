import { ApiProperty } from '@nestjs/swagger';
import { AccountResponseDto } from './AccountResponseDto';

export class AccountPageResponseDto {
  @ApiProperty({ description: 'List of accounts', type: [AccountResponseDto] })
  content!: AccountResponseDto[];

  @ApiProperty({ description: 'Total number of elements', example: 100 })
  totalElements!: number;

  @ApiProperty({ description: 'Total number of pages', example: 10 })
  totalPages!: number;

  @ApiProperty({ description: 'Current page number (0-indexed)', example: 0 })
  currentPage!: number;

  @ApiProperty({ description: 'Number of items per page', example: 10 })
  pageSize!: number;
}
