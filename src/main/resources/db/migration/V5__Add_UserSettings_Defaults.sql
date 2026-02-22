-- Migration to add default form settings to User Settings
ALTER TABLE user_settings 
ADD COLUMN default_metric_ids BIGINT[] DEFAULT ARRAY[]::BIGINT[];
