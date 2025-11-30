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

	// Additional comprehensive tests for edge cases
	describe('Edge cases and special scenarios', () => {
		it('handles empty string message', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

			customLogger('info', '');

			expect(logSpy).toHaveBeenCalledWith('[INFO] 2020-01-01T00:00:00.000Z - ');
		});

		it('handles message with only whitespace', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

			customLogger('info', '   ');

			expect(logSpy).toHaveBeenCalledWith('[INFO] 2020-01-01T00:00:00.000Z -    ');
		});

		it('handles null as additional argument', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

			customLogger('info', 'Message', null);

			expect(logSpy).toHaveBeenCalledWith('[INFO] 2020-01-01T00:00:00.000Z - Message', null);
		});

		it('handles undefined as additional argument', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

			customLogger('info', 'Message', undefined);

			// undefined should be filtered out per implementation
			expect(logSpy).toHaveBeenCalledWith('[INFO] 2020-01-01T00:00:00.000Z - Message');
		});

		it('handles multiple undefined arguments', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

			customLogger('info', 'Message', undefined, undefined, undefined);

			expect(logSpy).toHaveBeenCalledWith('[INFO] 2020-01-01T00:00:00.000Z - Message');
		});

		it('handles mix of defined and undefined arguments', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

			customLogger('info', 'Message', 'arg1', undefined, 'arg2', undefined);

			expect(logSpy).toHaveBeenCalledWith('[INFO] 2020-01-01T00:00:00.000Z - Message', 'arg1', 'arg2');
		});

		it('handles very long messages', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
			const longMessage = 'A'.repeat(10000);

			customLogger('info', longMessage);

			expect(logSpy).toHaveBeenCalledWith(`[INFO] 2020-01-01T00:00:00.000Z - ${longMessage}`);
		});

		it('handles messages with special characters', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
			const specialMessage = 'Message with émojis 🚀 and spëcial chârs <>&"\'';

			customLogger('info', specialMessage);

			expect(logSpy).toHaveBeenCalledWith(`[INFO] 2020-01-01T00:00:00.000Z - ${specialMessage}`);
		});

		it('handles messages with newlines', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
			const multilineMessage = 'Line 1\nLine 2\nLine 3';

			customLogger('info', multilineMessage);

			expect(logSpy).toHaveBeenCalledWith(`[INFO] 2020-01-01T00:00:00.000Z - ${multilineMessage}`);
		});

		it('handles messages with tabs', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
			const tabbedMessage = 'Col1\tCol2\tCol3';

			customLogger('info', tabbedMessage);

			expect(logSpy).toHaveBeenCalledWith(`[INFO] 2020-01-01T00:00:00.000Z - ${tabbedMessage}`);
		});

		it('handles numeric additional arguments', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

			customLogger('info', 'Status code', 200);

			expect(logSpy).toHaveBeenCalledWith('[INFO] 2020-01-01T00:00:00.000Z - Status code', 200);
		});

		it('handles boolean additional arguments', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

			customLogger('info', 'Success', true);

			expect(logSpy).toHaveBeenCalledWith('[INFO] 2020-01-01T00:00:00.000Z - Success', true);
		});

		it('handles array additional arguments', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
			const arr = [1, 2, 3];

			customLogger('info', 'Array', arr);

			expect(logSpy).toHaveBeenCalledWith('[INFO] 2020-01-01T00:00:00.000Z - Array', arr);
		});

		it('handles object additional arguments', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
			const obj = { key: 'value', nested: { data: 123 } };

			customLogger('info', 'Object', obj);

			expect(logSpy).toHaveBeenCalledWith('[INFO] 2020-01-01T00:00:00.000Z - Object', obj);
		});

		it('handles Error objects as additional arguments', () => {
			const errorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
			const err = new Error('Test error');

			customLogger('error', 'Error occurred', err);

			expect(errorSpy).toHaveBeenCalledWith('[ERROR] 2020-01-01T00:00:00.000Z - Error occurred', err);
		});

		it('handles multiple different types of arguments', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

			customLogger('info', 'Mixed', 'string', 123, true, { key: 'val' }, [1, 2]);

			expect(logSpy).toHaveBeenCalledWith(
				'[INFO] 2020-01-01T00:00:00.000Z - Mixed',
				'string',
				123,
				true,
				{ key: 'val' },
				[1, 2],
			);
		});

		it('formats timestamp correctly in ISO format', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

			customLogger('info', 'Check timestamp');

			const call = logSpy.mock.calls[0][0] as string;
			const timestampMatch = call.match(/\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z/);
			expect(timestampMatch).not.toBeNull();
		});

		it('uses correct case for log level in prefix', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
			const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});
			const errorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

			customLogger('info', 'Info test');
			customLogger('warn', 'Warn test');
			customLogger('error', 'Error test');

			expect((logSpy.mock.calls[0][0] as string).startsWith('[INFO]')).toBe(true);
			expect((warnSpy.mock.calls[0][0] as string).startsWith('[WARN]')).toBe(true);
			expect((errorSpy.mock.calls[0][0] as string).startsWith('[ERROR]')).toBe(true);
		});

		it('handles warn level with no additional arguments', () => {
			const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});

			customLogger('warn', 'Warning message');

			expect(warnSpy).toHaveBeenCalledWith('[WARN] 2020-01-01T00:00:00.000Z - Warning message');
		});

		it('handles error level with no additional arguments', () => {
			const errorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

			customLogger('error', 'Error message');

			expect(errorSpy).toHaveBeenCalledWith('[ERROR] 2020-01-01T00:00:00.000Z - Error message');
		});

		it('handles legacy call with multiple arguments', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

			customLogger('Legacy', 'arg1', 'arg2', 'arg3', 'arg4');

			expect(logSpy).toHaveBeenCalledWith(
				'[INFO] 2020-01-01T00:00:00.000Z - Legacy',
				'arg1',
				'arg2',
				'arg3',
				'arg4',
			);
		});

		it('handles zero as a valid additional argument', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

			customLogger('info', 'Count', 0);

			expect(logSpy).toHaveBeenCalledWith('[INFO] 2020-01-01T00:00:00.000Z - Count', 0);
		});

		it('handles empty string as additional argument', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

			customLogger('info', 'Message', '');

			expect(logSpy).toHaveBeenCalledWith('[INFO] 2020-01-01T00:00:00.000Z - Message', '');
		});

		it('handles false as a valid additional argument', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

			customLogger('info', 'Flag', false);

			expect(logSpy).toHaveBeenCalledWith('[INFO] 2020-01-01T00:00:00.000Z - Flag', false);
		});
	});

	describe('Console method verification', () => {
		it('calls console.log exactly once for info level', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

			customLogger('info', 'Test');

			expect(logSpy).toHaveBeenCalledTimes(1);
		});

		it('calls console.warn exactly once for warn level', () => {
			const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});

			customLogger('warn', 'Test');

			expect(warnSpy).toHaveBeenCalledTimes(1);
		});

		it('calls console.error exactly once for error level', () => {
			const errorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

			customLogger('error', 'Test');

			expect(errorSpy).toHaveBeenCalledTimes(1);
		});

		it('does not call console.warn or console.error for info level', () => {
			vi.spyOn(console, 'log').mockImplementation(() => {});
			const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});
			const errorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

			customLogger('info', 'Test');

			expect(warnSpy).not.toHaveBeenCalled();
			expect(errorSpy).not.toHaveBeenCalled();
		});

		it('does not call console.log or console.error for warn level', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
			vi.spyOn(console, 'warn').mockImplementation(() => {});
			const errorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

			customLogger('warn', 'Test');

			expect(logSpy).not.toHaveBeenCalled();
			expect(errorSpy).not.toHaveBeenCalled();
		});

		it('does not call console.log or console.warn for error level', () => {
			const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
			const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});
			vi.spyOn(console, 'error').mockImplementation(() => {});

			customLogger('error', 'Test');

			expect(logSpy).not.toHaveBeenCalled();
			expect(warnSpy).not.toHaveBeenCalled();
		});
	});
