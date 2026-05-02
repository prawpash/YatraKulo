import { Module } from '@nestjs/common';
import { TransactionModule } from './transaction/TransactionModule';

@Module({
  imports: [TransactionModule],
  controllers: [],
  providers: [],
})
export class AppModule {}
