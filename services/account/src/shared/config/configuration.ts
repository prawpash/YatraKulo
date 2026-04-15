import { parseNumber } from '@app/shared/utils/utils';

export interface APPConfig {
  appUrl: string;
  appEnv: string;
  port: number;
}

export interface DatabaseConfig {
  host: string;
  port: number;
  username: string;
  password: string;
  name: string;
  maxConnections: number;
}

export default () => ({
  appEnv: process.env.NODE_ENV ?? 'development',
  appUrl: process.env.APP_URL ?? 'http://localhost:3000',
  port: parseNumber(process.env.PORT, 3000, 'APP_PORT'),
  auth: {
    issuer: process.env.AUTH_ISSUER ?? 'http://localhost:5000',
    service_url: process.env.AUTH_SERVICE_URL ?? 'http://localhost:5000',
    jwks_uri: process.env.AUTH_JWKS_URI ?? 'http://localhost:5000/oauth2/jwks',
  },
  database: {
    host: process.env.DB_HOST ?? 'localhost',
    port: parseNumber(process.env.DB_PORT, 5432, 'DB_PORT'),
    username: process.env.DB_USER ?? 'postgres',
    password: process.env.DB_PASSWORD ?? 'postgres',
    name: process.env.DB_NAME ?? 'postgres',
    maxConnections: parseNumber(
      process.env.DB_MAX_CONNECTIONS,
      10,
      'DB_MAX_CONNECTIONS',
    ),
  },
});
