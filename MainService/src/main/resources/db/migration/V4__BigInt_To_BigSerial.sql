ALTER TABLE metrics_error DROP CONSTRAINT metrics_error_pkey;

ALTER TABLE metrics_error DROP COLUMN id;

ALTER TABLE metrics_error ADD COLUMN id BIGSERIAL PRIMARY KEY;