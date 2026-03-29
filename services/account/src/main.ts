import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { apiReference } from '@scalar/nestjs-api-reference';
import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { ConsoleLogger, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { APPConfig } from './shared/config/configuration';

async function bootstrap() {
  const logger = new Logger();

  const app = await NestFactory.create(AppModule, {
    logger: new ConsoleLogger({
      json: true,
      colors: true,
    }),
  });

  const configService = app.get(ConfigService<APPConfig>);

  const openAPIconfig = new DocumentBuilder()
    .setTitle('Cats example')
    .setDescription('The cats API description')
    .setVersion('1.0')
    .addTag('cats')
    .build();

  const document = SwaggerModule.createDocument(app, openAPIconfig);

  app.use(
    '/api-docs',
    apiReference({
      content: document,
    }),
  );

  await app.listen(configService.get<number>('port', 3000));

  logger.log(`Application is running on: ${await app.getUrl()}`);
}

void bootstrap();
