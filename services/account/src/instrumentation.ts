import { NodeSDK } from '@opentelemetry/sdk-node';
import { OTLPTraceExporter } from '@opentelemetry/exporter-trace-otlp-http';
import { PrometheusExporter } from '@opentelemetry/exporter-prometheus';
import { resourceFromAttributes } from '@opentelemetry/resources';
import { getNodeAutoInstrumentations } from '@opentelemetry/auto-instrumentations-node';

import * as dotenv from 'dotenv';
import * as path from 'path';

// Load .env variables before starting the SDK
dotenv.config({ path: path.resolve(__dirname, '../.env') });

const serviceName = process.env.OTEL_SERVICE_NAME || 'account-service';
const tracesEndpoint =
  process.env.OTEL_EXPORTER_OTLP_TRACES_ENDPOINT ||
  'http://localhost:4318/v1/traces';
const metricsPort = process.env.PROMETHEUS_METRICS_PORT
  ? parseInt(process.env.PROMETHEUS_METRICS_PORT, 10)
  : 9464;

const traceExporter = new OTLPTraceExporter({
  url: tracesEndpoint,
});

const metricExporter = new PrometheusExporter({
  port: metricsPort,
  endpoint: '/metrics',
});

const sdk = new NodeSDK({
  resource: resourceFromAttributes({
    'service.name': serviceName,
  }),
  traceExporter,
  metricReader: metricExporter,
  instrumentations: [
    getNodeAutoInstrumentations({
      '@opentelemetry/instrumentation-pino': {
        enabled: true,
        logKeys: {
          traceId: 'trace_id',
          spanId: 'span_id',
          traceFlags: 'trace_flags',
        },
      },
    }),
  ],
});

sdk.start();
console.log(`[OpenTelemetry] SDK initialized successfully. Prometheus metrics server listening on port ${metricsPort}`);

// Gracefully shut down the SDK on process exit
process.on('SIGTERM', () => {
  sdk
    .shutdown()
    .then(() => console.log('SDK shut down successfully'))
    .catch((err) => console.log('Error shutting down SDK', err))
    .finally(() => process.exit(0));
});
