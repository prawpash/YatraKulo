/**
 * Represents a paginated result set.
 * @template T - The type of items in the content
 */
export interface DomainPage<T> {
  content: readonly T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

/**
 * Creates a new DomainPage with mapped content.
 * @template T - The original type
 * @template R - The mapped type
 * @param page - The original DomainPage
 * @param mapper - Function to map from T to R
 * @returns A new DomainPage with mapped content
 */
export function mapDomainPage<T, R>(
  page: DomainPage<T>,
  mapper: (item: T) => R,
): DomainPage<R> {
  return {
    content: page.content.map(mapper) as readonly R[],
    totalElements: page.totalElements,
    totalPages: page.totalPages,
    currentPage: page.currentPage,
    pageSize: page.pageSize,
  };
}

/**
 * Creates a DomainPage instance with readonly content.
 * @template T - The type of items in the content
 * @param content - Array of items
 * @param totalElements - Total number of elements
 * @param totalPages - Total number of pages
 * @param currentPage - Current page number (0-indexed)
 * @param pageSize - Number of items per page
 * @returns A DomainPage instance
 */
export function createDomainPage<T>(
  content: T[],
  totalElements: number,
  totalPages: number,
  currentPage: number,
  pageSize: number,
): DomainPage<T> {
  return Object.freeze({
    content: Object.freeze(content) as readonly T[],
    totalElements,
    totalPages,
    currentPage,
    pageSize,
  });
}
