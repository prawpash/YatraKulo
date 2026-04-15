-- Seeder for account permissions
-- Maps to Permission enum in account service

INSERT INTO permission (code, description) VALUES
  ('account.read', 'Read account'),
  ('account.update', 'Update account'),
  ('account.create', 'Create account'),
  ('account.delete', 'Delete account')
ON CONFLICT (code) DO NOTHING;
