-- Seeder for workspace permissions
-- Maps to Permission enum in com.ekapasha.auth_service.role.domain.enums.Permission

INSERT INTO permission (code, description) VALUES
  ('workspace.update', 'Update workspace'),
  ('workspace.delete', 'Delete workspace'),
  ('workspace.invite', 'Invite user to workspace')
ON CONFLICT (code) DO NOTHING;
