-- Migration to seed additional comprehensive metrics and measurement types

-- 1. Add new measurement types if they don't exist
INSERT INTO measurement_types (name) 
VALUES ('Nutrition'), ('Activity'), ('Vitals')
ON CONFLICT (name) DO NOTHING;

-- 2. Seed common metrics under existing types
INSERT INTO metrics (measurement_type_id, name, base_unit, data_type) VALUES 
((SELECT id FROM measurement_types WHERE name = 'Weight'), 'Body Fat Percentage', '%', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Weight'), 'Muscle Mass', 'kg', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Weight'), 'Bone Mass', 'kg', 'NUMERIC'),

((SELECT id FROM measurement_types WHERE name = 'Length'), 'Height', 'cm', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Length'), 'Neck Size', 'cm', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Length'), 'Chest Size', 'cm', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Length'), 'Hip Size', 'cm', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Length'), 'Thigh Size', 'cm', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Length'), 'Calf Size', 'cm', 'NUMERIC');

-- 3. Seed metrics under new types
INSERT INTO metrics (measurement_type_id, name, base_unit, data_type) VALUES 
((SELECT id FROM measurement_types WHERE name = 'Vitals'), 'Heart Rate', 'bpm', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Vitals'), 'Systolic BP', 'mmHg', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Vitals'), 'Diastolic BP', 'mmHg', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Vitals'), 'Blood Oxygen', '%', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Vitals'), 'Respiratory Rate', 'br/min', 'NUMERIC');

INSERT INTO metrics (measurement_type_id, name, base_unit, data_type) VALUES 
((SELECT id FROM measurement_types WHERE name = 'Nutrition'), 'Water Intake', 'ml', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Nutrition'), 'Caffeine', 'mg', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Nutrition'), 'Fiber', 'g', 'NUMERIC');

INSERT INTO metrics (measurement_type_id, name, base_unit, data_type) VALUES 
((SELECT id FROM measurement_types WHERE name = 'Activity'), 'Step Count', 'steps', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Activity'), 'Active Minutes', 'min', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Activity'), 'Flights Climbed', 'floors', 'NUMERIC');
