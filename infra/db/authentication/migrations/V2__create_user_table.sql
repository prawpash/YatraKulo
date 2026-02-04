CREATE TABLE IF NOT EXISTS user (
  id UUID DEFAULT gen_random_uuid(),

  name VARCHAR(255) NOT NULL,
  username VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(100) NOT NULL,

  profile_picture_url TEXT,

  created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamptz DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT
    uq_user_email
  UNIQUE(email),

  CONSTRAINT
    uq_user_username
  UNIQUE(username),

  PRIMARY KEY (id)
);
