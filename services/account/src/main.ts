import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { apiReference } from '@scalar/nestjs-api-reference';
import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import {
  ConsoleLogger,
  ValidationPipe,
  ValidationError,
  BadRequestException,
} from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { APPConfig } from './shared/config/configuration';
import { GlobalExceptionFilter } from './GlobalExceptionFilter';

async function bootstrap() {
  const logger = new ConsoleLogger({
    json: process.env.NODE_ENV == 'production',
    colors: process.env.NODE_ENV !== 'production',
  });

  const app = await NestFactory.create(AppModule, {
    logger,
  });

  app.getHttpAdapter().getInstance().set('trust proxy', 1);

  app.setGlobalPrefix('api/v1');

  const configService = app.get(ConfigService<APPConfig>);

  const openAPIconfig = new DocumentBuilder()
    .addServer(configService.get<string>('appUrl', 'http://localhost:3000'))
    .setTitle('Account Service')
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

  logger.log(`Application is running on: ${await app.getUrl()}`);
}

void bootstrap();
