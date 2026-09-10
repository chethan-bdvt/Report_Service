ALTER TABLE report_evidence ADD COLUMN sha256_hash VARCHAR(64);

UPDATE report_evidence SET sha256_hash = "LEGACY";