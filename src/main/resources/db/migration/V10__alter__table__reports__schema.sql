UPDATE reports SET submission_type = 'ANONYMOUS'
WHERE submission_type IS NULL;

ALTER TABLE reports ALTER COLUMN submission_type SET NOT NULL;

ALTER TABLE reports ADD CONSTRAINT uk_reports_report_number UNIQUE(report_number);