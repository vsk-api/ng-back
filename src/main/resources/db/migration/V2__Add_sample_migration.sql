-- Sample migration to demonstrate Flyway functionality
-- This is an example of how future database changes should be implemented

-- Example: Adding a new column to existing table
-- ALTER TABLE pt_products ADD COLUMN description TEXT;

-- Example: Creating a new index
-- CREATE INDEX IF NOT EXISTS pt_products_lob_code_idx ON pt_products(lob, code);

-- Example: Adding a new table
-- CREATE TABLE IF NOT EXISTS pt_audit_log (
--   id BIGSERIAL PRIMARY KEY,
--   table_name VARCHAR(100) NOT NULL,
--   operation VARCHAR(20) NOT NULL,
--   old_values JSONB,
--   new_values JSONB,
--   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--   created_by VARCHAR(100)
-- );

-- This migration is currently empty but shows the structure for future migrations
SELECT 'Sample migration V2 executed' as message;
