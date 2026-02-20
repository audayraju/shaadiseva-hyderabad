-- V1__core_schema.sql
-- Core schema for ShaadiSeva Hyderabad

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR(255) UNIQUE,
    phone         VARCHAR(20)  UNIQUE,
    password_hash VARCHAR,
    role          VARCHAR(20)  NOT NULL DEFAULT 'CUSTOMER',
    status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMPTZ  DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  DEFAULT NOW(),
    CONSTRAINT chk_users_email_or_phone CHECK (email IS NOT NULL OR phone IS NOT NULL)
);

CREATE TABLE categories (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    is_active   BOOLEAN      DEFAULT true,
    created_at  TIMESTAMPTZ  DEFAULT NOW()
);

CREATE TABLE vendor_profiles (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID         NOT NULL REFERENCES users(id),
    category_id      UUID         REFERENCES categories(id),
    business_name    VARCHAR(200) NOT NULL,
    gst_number       VARCHAR(20),
    pan_number       VARCHAR(20),
    address          TEXT,
    city             VARCHAR(100),
    bio              TEXT,
    years_experience INT,
    created_at       TIMESTAMPTZ  DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  DEFAULT NOW()
);

CREATE TABLE vendor_applications (
    id                 UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id            UUID        NOT NULL REFERENCES users(id),
    vendor_profile_id  UUID        REFERENCES vendor_profiles(id),
    status             VARCHAR(30) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    rejection_reason   TEXT,
    reviewed_by        UUID        REFERENCES users(id),
    reviewed_at        TIMESTAMPTZ,
    created_at         TIMESTAMPTZ DEFAULT NOW(),
    updated_at         TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE vendor_documents (
    id                     UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    vendor_application_id  UUID         NOT NULL REFERENCES vendor_applications(id),
    doc_type               VARCHAR(50)  NOT NULL,
    file_name              VARCHAR(255) NOT NULL,
    storage_key            VARCHAR(500) NOT NULL,
    content_type           VARCHAR(100),
    file_size              BIGINT,
    uploaded_at            TIMESTAMPTZ  DEFAULT NOW()
);

CREATE TABLE wedding_requests (
    id          UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID           NOT NULL REFERENCES users(id),
    title       VARCHAR(200)   NOT NULL,
    wedding_date DATE,
    location    VARCHAR(200),
    budget_min  NUMERIC(15, 2),
    budget_max  NUMERIC(15, 2),
    status      VARCHAR(30)    NOT NULL DEFAULT 'OPEN',
    created_at  TIMESTAMPTZ    DEFAULT NOW(),
    updated_at  TIMESTAMPTZ    DEFAULT NOW()
);

CREATE TABLE request_items (
    id                 UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    wedding_request_id UUID        NOT NULL REFERENCES wedding_requests(id),
    category_id        UUID        REFERENCES categories(id),
    description        TEXT,
    quantity           INT         DEFAULT 1,
    created_at         TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE quotes (
    id                 UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    wedding_request_id UUID           NOT NULL REFERENCES wedding_requests(id),
    vendor_id          UUID           NOT NULL REFERENCES users(id),
    total_amount       NUMERIC(15, 2) NOT NULL,
    message            TEXT,
    status             VARCHAR(30)    NOT NULL DEFAULT 'PENDING',
    created_at         TIMESTAMPTZ    DEFAULT NOW(),
    updated_at         TIMESTAMPTZ    DEFAULT NOW()
);

CREATE TABLE quote_items (
    id          UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    quote_id    UUID           NOT NULL REFERENCES quotes(id),
    description VARCHAR(500)   NOT NULL,
    unit_price  NUMERIC(15, 2) NOT NULL,
    quantity    INT            DEFAULT 1,
    subtotal    NUMERIC(15, 2) NOT NULL
);

CREATE TABLE bookings (
    id                 UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    quote_id           UUID        NOT NULL REFERENCES quotes(id),
    customer_id        UUID        NOT NULL REFERENCES users(id),
    vendor_id          UUID        NOT NULL REFERENCES users(id),
    wedding_request_id UUID        NOT NULL REFERENCES wedding_requests(id),
    status             VARCHAR(30) NOT NULL DEFAULT 'CONFIRMED',
    confirmed_at       TIMESTAMPTZ DEFAULT NOW(),
    created_at         TIMESTAMPTZ DEFAULT NOW(),
    updated_at         TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE chat_threads (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id UUID        NOT NULL REFERENCES bookings(id),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE chat_messages (
    id        UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    thread_id UUID        NOT NULL REFERENCES chat_threads(id),
    sender_id UUID        NOT NULL REFERENCES users(id),
    content   TEXT        NOT NULL,
    sent_at   TIMESTAMPTZ DEFAULT NOW()
);
