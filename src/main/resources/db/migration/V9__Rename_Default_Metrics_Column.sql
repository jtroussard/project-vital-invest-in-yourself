-- Migration to rename default_metric_ids to default_journal_metric_ids
-- This provides better clarity on the field's purpose (Journal Entry shortcuts)

ALTER TABLE user_settings RENAME COLUMN default_metric_ids TO default_journal_metric_ids;
