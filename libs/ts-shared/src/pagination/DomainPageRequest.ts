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
export function createDomainPageRequest(
  page: number,
  size: number,
): DomainPageRequest {
  if (!Number.isInteger(page) || page < 0) {
    throw new Error("page must be a non-negative integer");
  }

  if (!Number.isInteger(size) || size <= 0) {
    throw new Error("size must be a positive integer");
  }

  if (size > 100) {
    throw new Error("size must be less than or equal to 100");
  }

  return Object.freeze({ page, size });
}
