-- Migration to refine metrics and categories to the minimum necessary
-- This corrects the over-seeding in V4 and V6 without breaking migration history

-- 1. Clean up existing data to start fresh (Safe as journal_entries is confirmed empty)
-- We use DELETE instead of TRUNCATE for better compatibility with some transactional environments
DELETE FROM journal_entries WHERE metric_id IS NOT NULL;
DELETE FROM metrics;
DELETE FROM measurement_types;

-- 2. Seed only requested categories
INSERT INTO measurement_types (name) VALUES 
('Weight'), 
('Ketones');

-- 3. Seed requested metrics
-- Weight Category
INSERT INTO metrics (measurement_type_id, name, base_unit, data_type) 
VALUES ((SELECT id FROM measurement_types WHERE name = 'Weight'), 'Weight', 'kg', 'NUMERIC');

-- Ketones Category
INSERT INTO metrics (measurement_type_id, name, base_unit, data_type) VALUES 
((SELECT id FROM measurement_types WHERE name = 'Ketones'), 'Blood Ketone', 'mmol/L', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Ketones'), 'Breath Ketone', 'ppm', 'NUMERIC');
