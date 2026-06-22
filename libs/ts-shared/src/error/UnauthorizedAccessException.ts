export class UnauthorizedAccessException extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'UnauthorizedAccessException';
  }
}
