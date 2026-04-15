import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { apiReference } from '@scalar/nestjs-api-reference';
import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { ConsoleLogger, Logger, ValidationPipe } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { APPConfig } from './shared/config/configuration';

async function bootstrap() {
  const logger = new Logger();

  const app = await NestFactory.create(AppModule, {
    logger: new ConsoleLogger({
      json: process.env.NODE_ENV == 'production',
      colors: process.env.NODE_ENV !== 'production',
    }),
  });

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

  app.useGlobalPipes(new ValidationPipe({ whitelist: true, transform: true }));

  app.enableShutdownHooks();

  await app.listen(configService.get<number>('port', 3000));

  logger.log(`Application is running on: ${await app.getUrl()}`);
}

void bootstrap();
