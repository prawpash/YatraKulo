import { afterEach, describe, expect, it, vi } from 'vitest';
import { customLogger } from '@/lib/customLogger';

describe('customLogger', () => {
	afterEach(() => {
		vi.restoreAllMocks();
	});

	it('logs the message and additional arguments', () => {
		const logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});

		customLogger('Hello', 'world', '!');

		expect(logSpy).toHaveBeenCalledWith('Hello', 'world', '!');
	});
});
