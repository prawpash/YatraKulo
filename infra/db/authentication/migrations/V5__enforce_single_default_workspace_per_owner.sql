-- V5__enforce_single_default_workspace_per_owner.sql
-- Enforces that each owner can have at most ONE workspace with is_default = TRUE

-- Step 1: Validate existing data before applying constraint
-- If any owner has multiple default workspaces, fail with a clear error
DO $$
DECLARE
  duplicate_count INTEGER;
BEGIN
  SELECT COUNT(*) INTO duplicate_count
  FROM (
    SELECT owner_id
    FROM workspace
    WHERE is_default = TRUE
    GROUP BY owner_id
    HAVING COUNT(*) > 1
  ) AS duplicates;

  IF duplicate_count > 0 THEN
    RAISE EXCEPTION 
      'Migration aborted: Found % owner(s) with multiple default workspaces. Please resolve duplicates before running this migration.', 
      duplicate_count;
  END IF;
END $$;

-- Step 2: Create partial unique index
-- Only indexes rows where is_default = TRUE, enforcing one default per owner
CREATE UNIQUE INDEX IF NOT EXISTS uq_workspace_owner_default 
ON workspace(owner_id) 
WHERE is_default = TRUE;
