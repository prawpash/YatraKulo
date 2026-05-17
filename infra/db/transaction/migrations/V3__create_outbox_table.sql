CREATE TABLE IF NOT EXISTS outbox (
  id UUID DEFAULT gen_random_uuid(),
  aggregate_type VARCHAR(255) NOT NULL,
  aggregate_id UUID NOT NULL,
  event_type VARCHAR(255) NOT NULL,
  payload JSONB NOT NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
  created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
  published_at timestamptz,

  PRIMARY KEY (id)
);

CREATE INDEX idx_outbox_status_created_at ON outbox (status, created_at) WHERE status = 'PENDING';
