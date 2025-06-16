CREATE TABLE metrics_error (
    id BIGINT PRIMARY KEY,
    kafka_topic VARCHAR(255) NOT NULL,
    time_executed BIGINT NOT NULL,
    method_signature VARCHAR(500) NOT NULL,
    message TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE metrics_error IS 'Table for storing metric execution errors';
COMMENT ON COLUMN metrics_error.id IS 'Primary key';
COMMENT ON COLUMN metrics_error.kafka_topic IS 'Kafka topic where message was supposed to be sent';
COMMENT ON COLUMN metrics_error.time_executed IS 'Method execution time in milliseconds';
COMMENT ON COLUMN metrics_error.method_signature IS 'Signature of the method that failed';
COMMENT ON COLUMN metrics_error.message IS 'Error message details';
COMMENT ON COLUMN metrics_error.created_at IS 'Timestamp when record was created';