-- Seeder for Owner role with all permissions
-- Creates a global 'Owner' role (workspace_id = NULL) and assigns all existing permissions

-- Step 1: Create Owner role if it doesn't exist
INSERT INTO role (name, description, workspace_id)
SELECT 'Owner', 'Workspace owner with all permissions', NULL
WHERE NOT EXISTS (
  SELECT 1 FROM role WHERE name = 'Owner' AND workspace_id IS NULL
);

-- Step 2: Assign all permissions to the Owner role
INSERT INTO role_permissions (role_id, permission_code)
SELECT r.id, p.code
FROM role r
CROSS JOIN permission p
WHERE r.name = 'Owner' AND r.workspace_id IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp 
    WHERE rp.role_id = r.id AND rp.permission_code = p.code
  );
