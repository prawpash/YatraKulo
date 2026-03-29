export class DomainRuleViolationException extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'DomainRuleViolationException';
  }
}