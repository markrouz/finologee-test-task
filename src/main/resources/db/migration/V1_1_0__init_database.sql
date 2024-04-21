CREATE TABLE "user"
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100)       NOT NULL,
    address  TEXT
);

INSERT INTO "user" (username, password, address)
VALUES ('user1', '$2a$10$nomNb6reMqMiKLjley4qNO77aRuekBXyFxnmdL0qc1ITCwNVv6qSm', '123 Example St, City, Country');

INSERT INTO "user" (username, password, address)
VALUES ('user2', '$2a$10$nomNb6reMqMiKLjley4qNO77aRuekBXyFxnmdL0qc1ITCwNVv6qSm', '124 Example St, City, Country');

INSERT INTO "user" (username, password, address)
VALUES ('user3', '$2a$10$nomNb6reMqMiKLjley4qNO77aRuekBXyFxnmdL0qc1ITCwNVv6qSm', '125 Example St, City, Country');

CREATE TYPE bank_account_status AS ENUM ('ENABLED', 'BLOCKED');

CREATE TABLE bank_account
(
    id             SERIAL PRIMARY KEY,
    account_number VARCHAR(34) UNIQUE NOT NULL,                       -- IBAN format
    account_name   VARCHAR(100)       NOT NULL,
    status         bank_account_status DEFAULT 'ENABLED',
    CONSTRAINT valid_iban_code CHECK (substring(account_number FROM 1 FOR 2) ~ '^[A-Z]{2}$'),
    CONSTRAINT valid_account_number_iban_length CHECK (length(account_number) <= 34) -- IBAN length check
);

CREATE TYPE balance_type AS ENUM ('END_OF_DAY', 'AVAILABLE');

CREATE TABLE balance
(
    id              SERIAL PRIMARY KEY,
    amount          NUMERIC(15, 2) NOT NULL,
    currency        VARCHAR(3)     NOT NULL,
    type            balance_type DEFAULT 'AVAILABLE',
    bank_account_id INT REFERENCES bank_account (id)
);

CREATE TABLE user_bank_account
(
    user_id         INT REFERENCES "user" (id),
    bank_account_id INT REFERENCES bank_account (id),
    PRIMARY KEY (user_id, bank_account_id)
);

INSERT INTO bank_account (account_number, account_name, status)
VALUES ('BE71096123456769', 'User 1, User 2 family Account 1', 'ENABLED'),
       ('BY86AKBB10100000002966000000', 'User 1, User 2 family Account 2', 'ENABLED');

INSERT INTO balance (amount, currency, bank_account_id)
VALUES (1000, 'EUR', 1);

INSERT INTO bank_account (account_number, account_name, status)
VALUES ('NO8330001234567', 'User 3 personal Account 1', 'ENABLED'),
       ('PL10105000997603123456789123', 'User 3 personal Account 2', 'ENABLED');

-- Populate user_bank_account relationship (at least two accounts per user)
INSERT INTO user_bank_account (user_id, bank_account_id)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (2, 2),
       (3, 3),
       (3, 4);


CREATE TYPE payment_status AS ENUM ('EXECUTED');

CREATE TABLE payment
(
    id                         SERIAL PRIMARY KEY,
    amount                     NUMERIC(15, 2) NOT NULL,
    currency                   VARCHAR(3)     NOT NULL,
    giver_bank_account_id      INT REFERENCES bank_account (id),
    beneficiary_account_number VARCHAR(34)    NOT NULL,                           -- IBAN format
    beneficiary_name           VARCHAR(100)   NOT NULL,
    communication              VARCHAR(100),
    creation_date              TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    status                     payment_status default 'EXECUTED',
    CONSTRAINT beneficiary_account_number CHECK (substring(beneficiary_account_number FROM 1 FOR 2) ~ '^[A-Z]{2}$'),
    CONSTRAINT valid_beneficiary_account_number_iban_length CHECK (length(beneficiary_account_number) <= 34) -- IBAN length check
);