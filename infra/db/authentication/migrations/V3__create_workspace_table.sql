CREATE TABLE IF NOT EXISTS workspace (
  id UUID DEFAULT gen_random_uuid(),

  name VARCHAR(255) NOT NULL,
  description TEXT,

  owner_id UUID NOT NULL,

  is_default BOOLEAN DEFAULT FALSE,

  created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamptz DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT
    fk_workspace_owner_id_user
  FOREIGN KEY(owner_id)
  REFERENCES "user"(id)
  ON DELETE CASCADE,

  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS workspace_members(
  id UUID DEFAULT gen_random_uuid(),

  workspace_id UUID NOT NULL,
  user_id UUID NOT NULL,
  role_id UUID NOT NULL,

  added_at timestamptz DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamptz DEFAULT CURRENT_TIMESTAMP,

  added_by UUID,
  updated_by UUID,

  CONSTRAINT
    fk_workspace_members_workspace_id_workspace
  FOREIGN KEY(workspace_id)
  REFERENCES workspace(id)
  ON DELETE CASCADE,

  CONSTRAINT
    fk_workspace_members_user_id_user
  FOREIGN KEY(user_id)
  REFERENCES "user"(id)
  ON DELETE CASCADE,

  CONSTRAINT
    fk_workspace_members_role_id_role
  FOREIGN KEY(role_id)
  REFERENCES role(id)
  ON DELETE CASCADE,

  CONSTRAINT
    uq_workspace_members_workspace_id_user_id_role_id
  UNIQUE(workspace_id, user_id, role_id),

  PRIMARY KEY (id)
);
