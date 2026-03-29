/**
 * Represents a pagination request.
 */
export interface DomainPageRequest {
  readonly page: number;
  readonly size: number;
}

/**
 * Creates a DomainPageRequest instance.
 * @param page - Page number (0-indexed)
 * @param size - Number of items per page
 * @returns A DomainPageRequest instance
 */
export function createDomainPageRequest(page: number, size: number): DomainPageRequest {
  return Object.freeze({ page, size });
}
