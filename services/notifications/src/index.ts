import { Hono } from 'hono';
import { logger } from 'hono/logger';
import { Resend } from 'resend';
import { customLogger } from '@/lib/customLogger';
import { sValidator } from '@hono/standard-validator';
import { SendEmailSchema } from './schemas/sendEmailSchema';

type Bindings = {
	RESEND_API_KEY: string;
	EMAIL_SENDER: string;
};

const app = new Hono<{ Bindings: Bindings }>();

app.use(logger(customLogger));

app.get('/', (c) => {
	return c.text('Hallo zusammen!');
});

app.post('/email', sValidator('json', SendEmailSchema), async (c) => {
	const jsonData = c.req.valid('json');

	const resend = new Resend(c.env.RESEND_API_KEY);

	const EMAIL_SENDER = c.env.EMAIL_SENDER;

	customLogger('info', `Start: sending email to ${jsonData.sendTo}`);

	try {
		const { data, error } = await resend.emails.send({
			from: EMAIL_SENDER,
			to: jsonData.sendTo,
			subject: jsonData.subject,
			html: jsonData.htmlBody,
		});

		if (error) {
			customLogger('error', 'Resend email provider error', error);
			return c.json({ message: 'Upstream email provider error' }, 502);
		}

		return c.json({ data }, 200);
	} catch (error) {
		customLogger('error', 'Failed to send email via Resend', error);
		return c.json({ message: 'Internal server error' }, 500);
	}
});

export default app;
