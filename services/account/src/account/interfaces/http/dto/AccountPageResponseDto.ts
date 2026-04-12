import { ApiProperty } from '@nestjs/swagger';
import { AccountResponseDto } from './AccountResponseDto';
import { Account } from '@app/account/domain/entity/Account';
import { DomainPage } from '@yk/shared';

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

  static fromDomainPage(page: DomainPage<Account>): AccountPageResponseDto {
    const dto = new AccountPageResponseDto();
    dto.content = page.content.map((a) => AccountResponseDto.fromDomain(a));
    dto.totalElements = page.totalElements;
    dto.totalPages = page.totalPages;
    dto.currentPage = page.currentPage;
    dto.pageSize = page.pageSize;
    return dto;
  }
}
