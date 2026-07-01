import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { apiReference } from '@scalar/nestjs-api-reference';
import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import {
  ValidationPipe,
  ValidationError,
  BadRequestException,
} from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { APPConfig } from './shared/config/configuration';
import { GlobalExceptionFilter } from './GlobalExceptionFilter';
import { AppLogger } from '@yk/shared';

async function bootstrap() {
  const logger = new AppLogger('TransactionService');

  const app = await NestFactory.create(AppModule, {
    logger: ['error', 'warn', 'log', 'debug', 'verbose'],
  });
  app.useLogger(logger);

  app.getHttpAdapter().getInstance().set('trust proxy', 1);

  app.setGlobalPrefix('api/v1');

  const configService = app.get(ConfigService<APPConfig>);

  const openAPIconfig = new DocumentBuilder()
    .addServer(configService.get<string>('appUrl', 'http://localhost:3000'))
    .setTitle('Transaction Service')
    .setVersion('1.0')
    .build();

  const document = SwaggerModule.createDocument(app, openAPIconfig);

  app.use(
    '/api-docs',
    apiReference({
      content: document,
    }),
  );

  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      transform: true,
      exceptionFactory: (errors: ValidationError[]) => {
        const formatErrors = (
          errors: ValidationError[],
          prefix = '',
        ): { field: string; message: string[] }[] => {
          return errors.reduce(
            (acc, err) => {
              const field = prefix ? `${prefix}.${err.property}` : err.property;

              if (err.constraints) {
                acc.push({
                  field,
                  message: Object.values(err.constraints),
                });
              }
              if (err.children && err.children.length > 0) {
                acc.push(...formatErrors(err.children, field));
              }
              return acc;
            },
            [] as { field: string; message: string[] }[],
          );
        };

        return new BadRequestException({
          message: formatErrors(errors),
          error: 'Bad Request',
          statusCode: 400,
        });
      },
    }),
  );

  app.useGlobalFilters(new GlobalExceptionFilter());

  app.enableShutdownHooks();

  await app.listen(configService.get<number>('port', 3000));

  logger.info(`Application is running on: ${await app.getUrl()}`);
}

void bootstrap();
