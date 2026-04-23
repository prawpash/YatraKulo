export class DuplicateDataException extends Error {
  constructor(message: string) {
    super(message);
    this.name = "DuplicateDataException";
  }
}
