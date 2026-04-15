CREATE TABLE IF NOT EXISTS account (
  id UUID DEFAULT gen_random_uuid(),

  name VARCHAR(100) NOT NULL,
  description TEXT,
  type VARCHAR(20) NOT NULL,

  workspace_id UUID,
  parent_id UUID,

  created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamptz DEFAULT CURRENT_TIMESTAMP,

  created_by UUID,
  updated_by UUID,

  deleted_at timestamptz,
  deleted_by UUID,

  CONSTRAINT
    fk_account_parent_id_account
  FOREIGN KEY(parent_id)
  REFERENCES account(id)
  ON DELETE CASCADE,

  PRIMARY KEY (id)
);
