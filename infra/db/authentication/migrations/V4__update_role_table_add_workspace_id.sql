ALTER TABLE role
ADD COLUMN workspace_id UUID,
ADD CONSTRAINT fk_role_workspace_id_workspace
  FOREIGN KEY (workspace_id)
  REFERENCES workspace(id)
  ON DELETE CASCADE;
