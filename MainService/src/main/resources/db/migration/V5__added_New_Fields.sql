ALTER TABLE transaction
    ADD COLUMN status VARCHAR(50) NOT NULL default 'REQUESTED',
    ADD COLUMN transaction_id UUID NOT NULL default gen_random_uuid(),
    ADD COLUMN timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE account
    ADD COLUMN status VARCHAR(50) NOT NULL default 'OPEN',
    ADD COLUMN account_id UUID NOT NULL DEFAULT gen_random_uuid(),
    ADD COLUMN frozen_amount DECIMAL(19,4) NOT NULL DEFAULT 0.0000;