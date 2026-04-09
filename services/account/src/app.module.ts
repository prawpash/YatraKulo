import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import configuration from './shared/config/configuration';
import { AccountModule } from './account/AccountModule';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true, load: [configuration] }),
    AccountModule,
  ],
  controllers: [],
  providers: [],
})
export class AppModule {}
