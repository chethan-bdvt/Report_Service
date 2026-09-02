-- reports_update.sql

-- Step 1: Add new columns
ALTER TABLE reports 
    ADD COLUMN report_number VARCHAR(20),
    ADD COLUMN submitted_by UUID,
    ADD COLUMN submission_type VARCHAR(20);