import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { customLogger } from '@/lib/customLogger';

const fixedDate = new Date('2020-01-01T00:00:00.000Z');

describe('customLogger', () => {
	beforeEach(() => {
		vi.useFakeTimers();
		vi.setSystemTime(fixedDate);
	});

	afterEach(() => {
		vi.restoreAllMocks();
		vi.useRealTimers();
	});

	it('logs info level messages', () => {
		const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

		customLogger('info', 'Hello', 'world');

		expect(logSpy).toHaveBeenCalledWith(
			'[INFO] 2020-01-01T00:00:00.000Z - Hello',
			'world',
		);
	});

	it('logs warn level messages', () => {
		const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});

		customLogger('warn', 'Heads up', { context: true });

		expect(warnSpy).toHaveBeenCalledWith(
			'[WARN] 2020-01-01T00:00:00.000Z - Heads up',
			{ context: true },
		);
	});

	it('logs error level messages', () => {
		const errorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
		const error = new Error('oh no');

		customLogger('error', 'Something failed', error);

		expect(errorSpy).toHaveBeenCalledWith(
			'[ERROR] 2020-01-01T00:00:00.000Z - Something failed',
			error,
		);
	});

	it('defaults to info level when called without explicit level', () => {
		const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

		customLogger('Legacy message', 'arg1', 'arg2');

		expect(logSpy).toHaveBeenCalledWith(
			'[INFO] 2020-01-01T00:00:00.000Z - Legacy message',
			'arg1',
			'arg2',
		);
	});
});
