ALTER TABLE documents
    ADD COLUMN processing_status VARCHAR(30) NOT NULL DEFAULT 'PENDING';

ALTER TABLE documents
    ADD COLUMN processed_at TIMESTAMP;