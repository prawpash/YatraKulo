CREATE TABLE IF NOT EXISTS transaction (
  id UUID DEFAULT gen_random_uuid(),

  amount DECIMAL(19,4) NOT NULL,
  note TEXT,
  from_account_id UUID NOT NULL,
  to_account_id UUID NOT NULL,
  idempotency_key VARCHAR(255) UNIQUE NOT NULL,
  workspace_id UUID NOT NULL,

  created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamptz DEFAULT CURRENT_TIMESTAMP,

  created_by UUID,
  updated_by UUID,

  deleted_at timestamptz,
  deleted_by UUID,

  PRIMARY KEY (id)
);
