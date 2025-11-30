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
