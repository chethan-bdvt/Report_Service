CREATE TABLE report_evidence (
	id UUID PRIMARY KEY,
	report_id UUID NOT NULL,
	file_name VARCHAR(200) NOT NULL,
	content_type VARCHAR(100) NOT NULL,
	file_size BIGINT NOT NULL,
	storage_key VARCHAR(500) NOT NULL,
	uploaded_at TIMESTAMP NOT NULL,
	
	CONSTRAINT fk_report_evidence_report_id 
	FOREIGN KEY (report_id)
	REFERENCES reports(id) 
	ON DELETE CASCADE
);

CREATE INDEX idx_report_evidence_report_id
ON report_evidence(report_id);