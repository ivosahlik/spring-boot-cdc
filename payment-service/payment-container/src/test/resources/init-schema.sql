DROP SCHEMA IF EXISTS payment CASCADE;

CREATE SCHEMA payment;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TYPE IF EXISTS payment.payment_status CASCADE;

CREATE TYPE payment.payment_status AS ENUM ('COMPLETED', 'CANCELLED', 'FAILED');

DROP TABLE IF EXISTS "payment".payments CASCADE;

CREATE TABLE "payment".payments
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    order_id uuid NOT NULL,
    price numeric(10,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status payment.payment_status NOT NULL,
    CONSTRAINT payments_pkey PRIMARY KEY (id)
);

DROP TABLE IF EXISTS "payment".credit_entry CASCADE;

CREATE TABLE "payment".credit_entry
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    total_credit_amount numeric(10,2) NOT NULL,
    CONSTRAINT credit_entry_pkey PRIMARY KEY (id)
);

DROP TYPE IF EXISTS payment.transaction_type CASCADE;

CREATE TYPE payment.transaction_type AS ENUM ('DEBIT', 'CREDIT');

DROP TABLE IF EXISTS "payment".credit_history CASCADE;

CREATE TABLE "payment".credit_history
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    amount numeric(10,2) NOT NULL,
    type payment.transaction_type NOT NULL,
    CONSTRAINT credit_history_pkey PRIMARY KEY (id)
);

DROP TYPE IF EXISTS payment.outbox_status CASCADE;
CREATE TYPE payment.outbox_status AS ENUM ('STARTED', 'COMPLETED', 'FAILED');

DROP TABLE IF EXISTS "payment".order_outbox CASCADE;

CREATE TABLE "payment".order_outbox
(
    id uuid NOT NULL,
    saga_id uuid NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    type character varying COLLATE pg_catalog."default" NOT NULL,
    payload jsonb NOT NULL,
    outbox_status payment.outbox_status NOT NULL,
    payment_status payment.payment_status NOT NULL,
    version integer NOT NULL,
    CONSTRAINT order_outbox_pkey PRIMARY KEY (id)
);

CREATE INDEX "payment_order_outbox_saga_status"
    ON "payment".order_outbox
    (type, payment_status);

CREATE UNIQUE INDEX "payment_order_outbox_saga_id_payment_status_outbox_status"
    ON "payment".order_outbox
    (type, saga_id, payment_status, outbox_status);

-- Test data
INSERT INTO payment.credit_entry(id, customer_id, total_credit_amount)
	VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb21', 'd215b5f8-0249-4dc5-89a3-51fd148cfb41', 50000.00);
INSERT INTO payment.credit_history(id, customer_id, amount, type)
	VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb23', 'd215b5f8-0249-4dc5-89a3-51fd148cfb41', 10000.00, 'CREDIT');
INSERT INTO payment.credit_history(id, customer_id, amount, type)
	VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb24', 'd215b5f8-0249-4dc5-89a3-51fd148cfb41', 60000.00, 'CREDIT');
INSERT INTO payment.credit_history(id, customer_id, amount, type)
	VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb25', 'd215b5f8-0249-4dc5-89a3-51fd148cfb41', 20000.00, 'DEBIT');

INSERT INTO payment.credit_entry(id, customer_id, total_credit_amount)
	VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb22', 'd215b5f8-0249-4dc5-89a3-51fd148cfb43', 100.00);
INSERT INTO payment.credit_history(id, customer_id, amount, type)
	VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb26', 'd215b5f8-0249-4dc5-89a3-51fd148cfb43', 100.00, 'CREDIT');
