-- V10: Create Journal Batches and link existing entries
-- This moves away from "timestamp-matching" grouping to proper relational Batch IDs.

-- 1. Create the journal_batches table
CREATE TABLE journal_batches (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    entry_date TIMESTAMPTZ NOT NULL,
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 2. Add batch_id to journal_entries
ALTER TABLE journal_entries ADD COLUMN batch_id BIGINT REFERENCES journal_batches(id);

-- 3. Migrate existing data: Create a batch for every unique (user_id, entry_date)
-- We use DISTINCT to find unique sets and insert them into the parent table.
INSERT INTO journal_batches (user_id, entry_date, created_at, updated_at)
SELECT DISTINCT user_id, entry_date, NOW(), NOW()
FROM journal_entries;

-- 4. Link entries to their new parent batches based on the old timestamp link
UPDATE journal_entries je
SET batch_id = jb.id
FROM journal_batches jb
WHERE je.user_id = jb.user_id 
AND je.entry_date = jb.entry_date;

-- 5. Add NOT NULL constraint once data is migrated
ALTER TABLE journal_entries ALTER COLUMN batch_id SET NOT NULL;

-- 6. Optional: Create an index for faster batch lookups
CREATE INDEX idx_journal_entries_batch_id ON journal_entries(batch_id);
CREATE INDEX idx_journal_batches_user_id_date ON journal_batches(user_id, entry_date);
