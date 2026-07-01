import { Module, NestModule, MiddlewareConsumer } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { ScheduleModule } from '@nestjs/schedule';
import configuration from './shared/config/configuration';
import { TransactionModule } from './transaction/TransactionModule';
import { LoggingMiddleware } from './shared/middleware/LoggingMiddleware';
import { DatabaseModule } from '@app/shared/config/DatabaseModule';
import { AuthModule } from '@app/shared/auth/AuthModule';
import { MessagingModule } from '@app/shared/messaging/MessagingModule';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true, load: [configuration] }),
    ScheduleModule.forRoot(),
    DatabaseModule,
    AuthModule,
    MessagingModule,
    TransactionModule,
  ],
  controllers: [],
  providers: [],
})
export class AppModule implements NestModule {
  configure(consumer: MiddlewareConsumer) {
    consumer.apply(LoggingMiddleware).forRoutes('*');
  }
}
