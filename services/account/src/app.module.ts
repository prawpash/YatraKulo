import { Module, NestModule, MiddlewareConsumer } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import configuration from './shared/config/configuration';
import { AccountModule } from './account/AccountModule';
import { LoggingMiddleware } from './shared/middleware/LoggingMiddleware';
import { DatabaseModule } from '@app/shared/config/DatabaseModule';
import { AuthModule } from '@app/shared/auth/AuthModule';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true, load: [configuration] }),
    DatabaseModule,
    AuthModule,
    AccountModule,
  ],
  controllers: [],
  providers: [],
})
export class AppModule implements NestModule {
  configure(consumer: MiddlewareConsumer) {
    consumer.apply(LoggingMiddleware).forRoutes('*');
  }
}
