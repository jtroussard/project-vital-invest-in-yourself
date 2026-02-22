-- Migration to create health data framework tables (Refined Naming, Expanded Profile & Nutrition)

-- 0. Update Profiles Table for expanded fields
ALTER TABLE profiles 
ADD COLUMN first_name VARCHAR(255),
ADD COLUMN middle_name VARCHAR(255),
ADD COLUMN last_name VARCHAR(255),
ADD COLUMN gender VARCHAR(50),
ADD COLUMN status VARCHAR(100),
ADD COLUMN address_street1 VARCHAR(255),
ADD COLUMN address_street2 VARCHAR(255),
ADD COLUMN address_city VARCHAR(100),
ADD COLUMN address_state_province VARCHAR(100),
ADD COLUMN address_postal_code VARCHAR(20),
ADD COLUMN address_country VARCHAR(100),
ADD COLUMN phone_country_code VARCHAR(10),
ADD COLUMN phone_area_code VARCHAR(10),
ADD COLUMN phone_number VARCHAR(20),
ADD COLUMN phone_extension VARCHAR(10);

-- 1. User Settings
CREATE TABLE user_settings (
    user_id UUID PRIMARY KEY REFERENCES profiles(user_id) ON DELETE CASCADE,
    preferred_unit_system VARCHAR(20) DEFAULT 'METRIC',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. User Biometrics
CREATE TABLE user_biometrics (
    user_id UUID PRIMARY KEY REFERENCES profiles(user_id) ON DELETE CASCADE,
    birth_date DATE,
    height FLOAT,
    current_weight FLOAT,
    target_weight FLOAT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Measurement Types
CREATE TABLE measurement_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- 4. Metrics
CREATE TABLE metrics (
    id BIGSERIAL PRIMARY KEY,
    measurement_type_id BIGINT REFERENCES measurement_types(id),
    name VARCHAR(100) UNIQUE NOT NULL,
    base_unit VARCHAR(10) NOT NULL,
    data_type VARCHAR(20) DEFAULT 'NUMERIC'
);

-- 5. Meals (Complex entries)
CREATE TABLE meals (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    notes TEXT,
    total_calories FLOAT DEFAULT 0,
    total_protein FLOAT DEFAULT 0,
    total_carbs FLOAT DEFAULT 0,
    total_fat FLOAT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6. Meal Items
CREATE TABLE meal_items (
    id BIGSERIAL PRIMARY KEY,
    meal_id BIGINT NOT NULL REFERENCES meals(id) ON DELETE CASCADE,
    food_name VARCHAR(255) NOT NULL,
    calories FLOAT DEFAULT 0,
    protein FLOAT DEFAULT 0,
    carbs FLOAT DEFAULT 0,
    fat FLOAT DEFAULT 0,
    quantity FLOAT DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 7. Journal Entries (Universal Container)
CREATE TABLE journal_entries (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES profiles(user_id) ON DELETE CASCADE,
    metric_id BIGINT REFERENCES metrics(id),
    meal_id BIGINT REFERENCES meals(id),
    entry_type VARCHAR(20) NOT NULL DEFAULT 'METRIC', -- METRIC, MEAL, NOTE
    value FLOAT, -- Only used for METRIC types
    notes TEXT,
    entry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_journal_entries_user_id ON journal_entries(user_id);
CREATE INDEX idx_journal_entries_entry_date ON journal_entries(entry_date);

-- Seed Initial Data
INSERT INTO measurement_types (name) VALUES 
('Weight'), 
('Length'), 
('Blood Pressure'), 
('Concentration'), 
('Temperature'),
('Mood');

INSERT INTO metrics (measurement_type_id, name, base_unit, data_type) VALUES 
((SELECT id FROM measurement_types WHERE name = 'Weight'), 'Body Weight', 'kg', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Length'), 'Waist Size', 'cm', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Concentration'), 'Blood Ketone', 'mmol/L', 'NUMERIC'),
((SELECT id FROM measurement_types WHERE name = 'Mood'), 'Happiness Level', 'rating', 'SCALE'),
((SELECT id FROM measurement_types WHERE name = 'Mood'), 'Stress Level', 'rating', 'SCALE');
