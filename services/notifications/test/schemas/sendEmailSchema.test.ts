import { describe, expect, it } from 'vitest';
import { parse } from 'valibot';
import { SendEmailSchema } from '../../src/schemas/sendEmailSchema';

describe('SendEmailSchema', () => {
	it('accepts valid email payloads', () => {
		const payload = {
			sendTo: 'user@example.com',
			subject: 'Greetings',
			htmlBody: '<p>Hello!</p>',
		};

		expect(parse(SendEmailSchema, payload)).toEqual(payload);
	});

	it('rejects invalid email payloads', () => {
		const invalidPayload = {
			sendTo: 'not-an-email',
			subject: 'Nope',
			htmlBody: '<p>Invalid email</p>',
		};

		expect(() => parse(SendEmailSchema, invalidPayload)).toThrow();
	});
});
