import { ApiProperty } from '@nestjs/swagger';
import { DomainPage } from '@yk/shared';
import { Transaction } from '@app/transaction/domain/entity/Transaction';
import { TransactionResponseDto } from './TransactionResponseDto';

export class TransactionPageResponseDto {
  @ApiProperty({ type: [TransactionResponseDto] })
  content!: TransactionResponseDto[];

  @ApiProperty({ description: 'Total number of elements', example: 100 })
  totalElements!: number;

  @ApiProperty({ description: 'Total number of pages', example: 10 })
  totalPages!: number;

  @ApiProperty({ description: 'Current page number (0-indexed)', example: 0 })
  currentPage!: number;

  @ApiProperty({ description: 'Number of items per page', example: 10 })
  pageSize!: number;

  static fromDomainPage(
    domainPage: DomainPage<Transaction>,
  ): TransactionPageResponseDto {
    const dto = new TransactionPageResponseDto();

    dto.content = domainPage.content.map((item) =>
      TransactionResponseDto.fromDomain(item),
    );

    dto.totalElements = domainPage.totalElements;
    dto.totalPages = domainPage.totalPages;
    dto.currentPage = domainPage.currentPage;
    dto.pageSize = domainPage.pageSize;
    return dto;
  }
}
