CREATE TABLE IF NOT EXISTS client (
    id BIGINT PRIMARY KEY,
    last_name VARCHAR(255),
    first_name VARCHAR(255),
    middle_name VARCHAR(255),
    client_id BIGINT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS account (
    id BIGSERIAL PRIMARY KEY,
    client_id INTEGER NOT NULL REFERENCES client(id) ON DELETE CASCADE,
    type VARCHAR(10) NOT NULL,
    balance DECIMAL NOT NULL
);

CREATE TABLE IF NOT EXISTS "transaction" (
    id BIGINT PRIMARY KEY,
    account_id INTEGER NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    transaction_sum NUMERIC(19,2) NOT NULL,
    transaction_time TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS data_source_error_log (
    id BIGSERIAL PRIMARY KEY,
    stack_trace TEXT NOT NULL,
    message VARCHAR(255) NOT NULL,
    method_signature TEXT NOT NULL
);

