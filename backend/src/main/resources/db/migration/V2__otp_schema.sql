-- V2__otp_schema.sql

CREATE TABLE otp_codes (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    phone      VARCHAR(20)  NOT NULL,
    code_hash  VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ  NOT NULL,
    used       BOOLEAN      DEFAULT false,
    created_at TIMESTAMPTZ  DEFAULT NOW()
);

CREATE INDEX idx_otp_codes_phone ON otp_codes(phone);
