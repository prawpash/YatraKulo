import { Module } from '@nestjs/common';
import { ScheduleModule } from '@nestjs/schedule';
import { TransactionModule } from './transaction/TransactionModule';

@Module({
  imports: [ScheduleModule.forRoot(), TransactionModule],
  controllers: [],
  providers: [],
})
export class AppModule {}
