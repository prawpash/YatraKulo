type LogLevel = 'info' | 'warn' | 'error';

export const customLogger = (
	levelOrMessage: LogLevel | string,
	messageOrRest?: string | unknown,
	...rest: unknown[]
) => {
	const isLevel = levelOrMessage === 'info' || levelOrMessage === 'warn' || levelOrMessage === 'error';
	const level: LogLevel = isLevel ? (levelOrMessage as LogLevel) : 'info';
	const message = isLevel ? (messageOrRest as string) : (levelOrMessage as string);
	const extra = isLevel ? rest : [messageOrRest, ...rest].filter((value) => value !== undefined);

	const timestamp = new Date().toISOString();
	const prefix = `[${level.toUpperCase()}] ${timestamp} - ${message}`;

	if (level === 'error') {
		console.error(prefix, ...extra);
		return;
	}

	if (level === 'warn') {
		console.warn(prefix, ...extra);
		return;
	}

	console.log(prefix, ...extra);
};
