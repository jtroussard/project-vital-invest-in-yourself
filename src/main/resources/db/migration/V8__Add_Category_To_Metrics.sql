-- Migration to add category column to metrics table for measurement strategy support
-- KISS: Only updates strictly existing metrics based on project database state.

-- 1. Add the column with a default of 'SCALAR'
ALTER TABLE metrics ADD COLUMN category VARCHAR(50) DEFAULT 'SCALAR' NOT NULL;

-- 2. Update existing 'Weight' metric to 'MASS'
UPDATE metrics SET category = 'MASS' WHERE name = 'Weight';
