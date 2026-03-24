CREATE TABLE IF NOT EXISTS permission (
  code VARCHAR(100), 
  description VARCHAR(100) NOT NULL,

  PRIMARY KEY (code)
);

CREATE TABLE IF NOT EXISTS role (
  id UUID DEFAULT gen_random_uuid(),
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255),

  created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamptz DEFAULT CURRENT_TIMESTAMP,

  created_by UUID,
  updated_by UUID,

  deleted_at timestamptz,
  deleted_by UUID,

  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS role_permissions(
  id UUID DEFAULT gen_random_uuid(),
  role_id UUID NOT NULL,
  permission_code VARCHAR(100) NOT NULL,

  added_at timestamptz DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT 
    fk_role_permissions_role_id_role 
  FOREIGN KEY(role_id) 
  REFERENCES role(id)
  ON DELETE CASCADE,

  CONSTRAINT 
    fk_role_permissions_permission_code_permission 
  FOREIGN KEY(permission_code) 
  REFERENCES permission(code)
  ON DELETE CASCADE,

  CONSTRAINT
    uq_role_permissions_role_id_permission_code
  UNIQUE(role_id, permission_code),

  PRIMARY KEY (id)
);
