import { Module, Global } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { OutboxRelayService } from './OutboxRelayService';
import { OutboxCleanupService } from './OutboxCleanupService';

@Global()
@Module({
  imports: [
    ClientsModule.registerAsync([
      {
        name: 'RABBITMQ_SERVICE',
        imports: [ConfigModule],
        useFactory: (configService: ConfigService) => ({
          transport: Transport.RMQ,
          options: {
            urls: [
              configService.get<string>(
                'RABBITMQ_URL',
                'amqp://localhost:5672',
              ),
            ],
            queue: 'transaction_queue',
            queueOptions: {
              durable: true,
            },
          },
        }),
        inject: [ConfigService],
      },
    ]),
  ],
  providers: [OutboxRelayService, OutboxCleanupService],
  exports: [OutboxRelayService, OutboxCleanupService],
})
export class MessagingModule {}
