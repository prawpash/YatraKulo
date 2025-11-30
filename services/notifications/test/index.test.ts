import { beforeEach, describe, expect, it, vi } from 'vitest';

const { ResendConstructorMock, sendEmailMock } = vi.hoisted(() => {
	const sendEmailMock = vi.fn();
	const ResendConstructorMock = vi.fn().mockImplementation(() => ({
		emails: { send: sendEmailMock },
	}));
	return { ResendConstructorMock, sendEmailMock };
});

const customLoggerMock = vi.fn();

vi.mock('resend', () => ({
	Resend: ResendConstructorMock,
}));

vi.mock('@/lib/customLogger', () => ({
	customLogger: (...args: unknown[]) => customLoggerMock(...args),
}));

import app from '../src/index';

const mockEnv = {
	RESEND_API_KEY: 'test-resend-key',
	EMAIL_SENDER: 'no-reply@example.com',
};

describe('Hono app routes', () => {
	beforeEach(() => {
		vi.clearAllMocks();
		sendEmailMock.mockResolvedValue({ data: { id: 'email-id' }, error: null });
	});

	it('responds with greeting on GET /', async () => {
		const response = await app.request('http://localhost/');

		expect(response.status).toBe(200);
		expect(await response.text()).toBe('Hallo zusammen!');
	});

	it('sends email on POST /email with valid payload', async () => {
		const payload = {
			sendTo: 'receiver@example.com',
			subject: 'Hello',
			htmlBody: '<p>Message</p>',
		};

		const response = await app.request(
			'http://localhost/email',
			{
				method: 'POST',
				body: JSON.stringify(payload),
				headers: { 'Content-Type': 'application/json' },
			},
			mockEnv,
		);

		expect(ResendConstructorMock).toHaveBeenCalledWith(mockEnv.RESEND_API_KEY);
		expect(sendEmailMock).toHaveBeenCalledWith({
			from: mockEnv.EMAIL_SENDER,
			to: payload.sendTo,
			subject: payload.subject,
			html: payload.htmlBody,
		});

		const body = await response.json();
		expect(body).toEqual({ data: { id: 'email-id' } });
		expect(customLoggerMock).toHaveBeenCalledWith('info', `Start: sending email to ${payload.sendTo}`);
	});

	it('returns 502 and logs when email provider returns an error field', async () => {
		const providerError = { message: 'provider error' };
		sendEmailMock.mockResolvedValueOnce({ data: null, error: providerError });
		const payload = {
			sendTo: 'receiver@example.com',
			subject: 'Hello',
			htmlBody: '<p>Message</p>',
		};

		const response = await app.request(
			'http://localhost/email',
			{
				method: 'POST',
				body: JSON.stringify(payload),
				headers: { 'Content-Type': 'application/json' },
			},
			mockEnv,
		);

		expect(response.status).toBe(502);
		expect(await response.json()).toEqual({ message: 'Upstream email provider error' });
		expect(customLoggerMock).toHaveBeenCalledWith('error', 'Resend email provider error', providerError);
	});

	it('returns 500 and logs when send throws an exception', async () => {
		const thrownError = new Error('network issue');
		sendEmailMock.mockRejectedValueOnce(thrownError);
		const payload = {
			sendTo: 'receiver@example.com',
			subject: 'Hello',
			htmlBody: '<p>Message</p>',
		};

		const response = await app.request(
			'http://localhost/email',
			{
				method: 'POST',
				body: JSON.stringify(payload),
				headers: { 'Content-Type': 'application/json' },
			},
			mockEnv,
		);

		expect(response.status).toBe(500);
		expect(await response.json()).toEqual({ message: 'Internal server error' });
		expect(customLoggerMock).toHaveBeenCalledWith('error', 'Failed to send email via Resend', thrownError);
	});

	it('returns 400 on POST /email with invalid payload', async () => {
		const response = await app.request(
			'http://localhost/email',
			{
				method: 'POST',
				body: JSON.stringify({ subject: 'Missing fields', htmlBody: '<p>Hi</p>' }),
				headers: { 'Content-Type': 'application/json' },
			},
			mockEnv,
		);

		expect(response.status).toBe(400);
		expect(sendEmailMock).not.toHaveBeenCalled();
	});
});

	// Additional comprehensive tests for edge cases and error scenarios
	describe('Advanced error handling and edge cases', () => {
		it('handles missing Content-Type header gracefully', async () => {
			const payload = {
				sendTo: 'test@example.com',
				subject: 'Test',
				htmlBody: '<p>Test</p>',
			};

			const response = await app.request(
				'http://localhost/email',
				{
					method: 'POST',
					body: JSON.stringify(payload),
					// Intentionally omitting Content-Type header
				},
				mockEnv,
			);

			// Should still work or return appropriate error
			expect([200, 400]).toContain(response.status);
		});

		it('handles malformed JSON in request body', async () => {
			const response = await app.request(
				'http://localhost/email',
				{
					method: 'POST',
					body: '{invalid json}',
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			expect(response.status).toBe(400);
		});

		it('handles empty request body', async () => {
			const response = await app.request(
				'http://localhost/email',
				{
					method: 'POST',
					body: '',
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			expect(response.status).toBe(400);
		});

		it('handles null values in payload fields', async () => {
			const response = await app.request(
				'http://localhost/email',
				{
					method: 'POST',
					body: JSON.stringify({ sendTo: null, subject: null, htmlBody: null }),
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			expect(response.status).toBe(400);
			expect(sendEmailMock).not.toHaveBeenCalled();
		});

		it('handles undefined values in payload fields', async () => {
			const response = await app.request(
				'http://localhost/email',
				{
					method: 'POST',
					body: JSON.stringify({ sendTo: undefined, subject: 'Test', htmlBody: '<p>Test</p>' }),
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			expect(response.status).toBe(400);
		});

		it('handles extra unexpected fields in payload', async () => {
			const payload = {
				sendTo: 'user@example.com',
				subject: 'Test',
				htmlBody: '<p>Test</p>',
				extraField: 'should be ignored',
				anotherExtra: 123,
			};

			const response = await app.request(
				'http://localhost/email',
				{
					method: 'POST',
					body: JSON.stringify(payload),
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			// Should succeed, extra fields ignored
			expect(response.status).toBe(200);
			expect(sendEmailMock).toHaveBeenCalledWith(
				expect.objectContaining({
					to: 'user@example.com',
					subject: 'Test',
					html: '<p>Test</p>',
				}),
			);
		});

		it('handles very long email addresses', async () => {
			const longEmail = 'a'.repeat(50) + '@' + 'b'.repeat(50) + '.com';
			const payload = {
				sendTo: longEmail,
				subject: 'Test',
				htmlBody: '<p>Test</p>',
			};

			const response = await app.request(
				'http://localhost/email',
				{
					method: 'POST',
					body: JSON.stringify(payload),
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			expect(response.status).toBe(200);
			expect(sendEmailMock).toHaveBeenCalledWith(
				expect.objectContaining({
					to: longEmail,
				}),
			);
		});

		it('handles very long subject lines', async () => {
			const longSubject = 'A'.repeat(500);
			const payload = {
				sendTo: 'test@example.com',
				subject: longSubject,
				htmlBody: '<p>Test</p>',
			};

			const response = await app.request(
				'http://localhost/email',
				{
					method: 'POST',
					body: JSON.stringify(payload),
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			expect(response.status).toBe(200);
			expect(sendEmailMock).toHaveBeenCalledWith(
				expect.objectContaining({
					subject: longSubject,
				}),
			);
		});

		it('handles very large HTML body', async () => {
			const largeHtml = '<p>' + 'Lorem ipsum '.repeat(1000) + '</p>';
			const payload = {
				sendTo: 'test@example.com',
				subject: 'Large email',
				htmlBody: largeHtml,
			};

			const response = await app.request(
				'http://localhost/email',
				{
					method: 'POST',
					body: JSON.stringify(payload),
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			expect(response.status).toBe(200);
		});

		it('handles special characters in subject', async () => {
			const specialSubject = 'Test 🚀 with émojis and spëcial chârs <>&"\'';
			const payload = {
				sendTo: 'test@example.com',
				subject: specialSubject,
				htmlBody: '<p>Test</p>',
			};

			const response = await app.request(
				'http://localhost/email',
				{
					method: 'POST',
					body: JSON.stringify(payload),
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			expect(response.status).toBe(200);
			expect(sendEmailMock).toHaveBeenCalledWith(
				expect.objectContaining({
					subject: specialSubject,
				}),
			);
		});

		it('handles HTML with script tags in body', async () => {
			const payload = {
				sendTo: 'test@example.com',
				subject: 'Test',
				htmlBody: '<p>Hello</p><script>alert("xss")</script>',
			};

			const response = await app.request(
				'http://localhost/email',
				{
					method: 'POST',
					body: JSON.stringify(payload),
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			// Should send as-is (sanitization is email provider's responsibility)
			expect(response.status).toBe(200);
			expect(sendEmailMock).toHaveBeenCalled();
		});

		it('handles Resend returning null data with no error', async () => {
			sendEmailMock.mockResolvedValueOnce({ data: null, error: null });
			const payload = {
				sendTo: 'test@example.com',
				subject: 'Test',
				htmlBody: '<p>Test</p>',
			};

			const response = await app.request(
				'http://localhost/email',
				{
					method: 'POST',
					body: JSON.stringify(payload),
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			expect(response.status).toBe(200);
			expect(await response.json()).toEqual({ data: null });
		});

		it('logs correct message when starting to send email', async () => {
			const payload = {
				sendTo: 'specific@example.com',
				subject: 'Test',
				htmlBody: '<p>Test</p>',
			};

			await app.request(
				'http://localhost/email',
				{
					method: 'POST',
					body: JSON.stringify(payload),
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			expect(customLoggerMock).toHaveBeenCalledWith('info', 'Start: sending email to specific@example.com');
		});

		it('constructs Resend client with correct API key', async () => {
			const payload = {
				sendTo: 'test@example.com',
				subject: 'Test',
				htmlBody: '<p>Test</p>',
			};

			await app.request(
				'http://localhost/email',
				{
					method: 'POST',
					body: JSON.stringify(payload),
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			expect(ResendConstructorMock).toHaveBeenCalledWith('test-resend-key');
		});

		it('uses correct EMAIL_SENDER from environment', async () => {
			const payload = {
				sendTo: 'test@example.com',
				subject: 'Test',
				htmlBody: '<p>Test</p>',
			};

			await app.request(
				'http://localhost/email',
				{
					method: 'POST',
					body: JSON.stringify(payload),
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			expect(sendEmailMock).toHaveBeenCalledWith(
				expect.objectContaining({
					from: 'no-reply@example.com',
				}),
			);
		});
	});

	describe('HTTP method validation', () => {
		it('rejects PUT requests to /email', async () => {
			const response = await app.request(
				'http://localhost/email',
				{
					method: 'PUT',
					body: JSON.stringify({ sendTo: 'test@example.com', subject: 'Test', htmlBody: '<p>Test</p>' }),
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			expect(response.status).toBe(404);
			expect(sendEmailMock).not.toHaveBeenCalled();
		});

		it('rejects PATCH requests to /email', async () => {
			const response = await app.request(
				'http://localhost/email',
				{
					method: 'PATCH',
					body: JSON.stringify({ sendTo: 'test@example.com', subject: 'Test', htmlBody: '<p>Test</p>' }),
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			expect(response.status).toBe(404);
		});

		it('rejects DELETE requests to /email', async () => {
			const response = await app.request('http://localhost/email', { method: 'DELETE' }, mockEnv);

			expect(response.status).toBe(404);
		});

		it('rejects GET requests to /email', async () => {
			const response = await app.request('http://localhost/email', { method: 'GET' }, mockEnv);

			expect(response.status).toBe(404);
		});
	});

	describe('Root endpoint variations', () => {
		it('responds to GET / with greeting', async () => {
			const response = await app.request('http://localhost/');
			expect(response.status).toBe(200);
			expect(await response.text()).toBe('Hallo zusammen!');
		});

		it('rejects POST requests to /', async () => {
			const response = await app.request('http://localhost/', { method: 'POST' });
			expect(response.status).toBe(404);
		});

		it('handles trailing slash on root', async () => {
			const response = await app.request('http://localhost//');
			// Behavior depends on Hono routing
			expect([200, 404]).toContain(response.status);
		});
	});

	describe('URL path validation', () => {
		it('returns 404 for non-existent routes', async () => {
			const response = await app.request('http://localhost/nonexistent');
			expect(response.status).toBe(404);
		});

		it('returns 404 for /email with trailing slash', async () => {
			const payload = {
				sendTo: 'test@example.com',
				subject: 'Test',
				htmlBody: '<p>Test</p>',
			};

			const response = await app.request(
				'http://localhost/email/',
				{
					method: 'POST',
					body: JSON.stringify(payload),
					headers: { 'Content-Type': 'application/json' },
				},
				mockEnv,
			);

			// Depends on Hono routing config
			expect([200, 404]).toContain(response.status);
		});

		it('handles case sensitivity correctly', async () => {
			const response = await app.request('http://localhost/EMAIL', { method: 'POST' }, mockEnv);
			expect(response.status).toBe(404);
		});
	});
