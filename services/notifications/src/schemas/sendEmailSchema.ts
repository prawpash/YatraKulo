import { email, InferOutput, nonEmpty, object, pipe, string } from 'valibot';

// send email schema
export const SendEmailSchema = object({
	sendTo: pipe(
		string('Target email must be a string.'),
		nonEmpty('Do not pass empty string.'),
		email('Target email is invalid format.'),
	),
	subject: pipe(
		string('Subject must be a string'),
		nonEmpty('Do not pass empty string.'),
	),
	htmlBody: pipe(
		string('htmlBody must be a string'),
		nonEmpty('Do not pass empty string.'),
	),
});

export type SendEmailSchemaType = InferOutput<typeof SendEmailSchema>;
