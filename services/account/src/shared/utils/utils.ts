export const parseNumber = (
  value: string | undefined,
  fallback: number,
  key: string,
): number => {
  const parsed = Number.parseInt(value ?? fallback.toString(), 10);

  if (Number.isNaN(parsed)) {
    throw new Error(`Invalid numeric value for ${key}`);
  }

  return parsed;
};
