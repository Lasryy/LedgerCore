CREATE TABLE ledgers (
    id UUID PRIMARY KEY DEFAULT uuidv7(),

    name VARCHAR(120) NOT NULL,

    currency CHAR(3) NOT NULL
        CHECK (currency ~ '^[A-Z]{3}$'),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE accounts (
    id UUID PRIMARY KEY DEFAULT uuidv7(),

    ledger_id UUID NOT NULL,

    code VARCHAR(64) NOT NULL,

    name VARCHAR(120) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_accounts_ledger
        FOREIGN KEY (ledger_id)
        REFERENCES ledgers(id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_accounts_ledger_code
        UNIQUE (ledger_id, code)
);


CREATE TABLE ledger_transactions (
    id UUID PRIMARY KEY DEFAULT uuidv7(),

    ledger_id UUID NOT NULL,

    description TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ledger_transactions_ledger
        FOREIGN KEY (ledger_id)
        REFERENCES ledgers(id)
        ON DELETE RESTRICT
);


CREATE TABLE entries (
    id UUID PRIMARY KEY DEFAULT uuidv7(),

    transaction_id UUID NOT NULL,

    account_id UUID NOT NULL,

    amount NUMERIC(19, 4) NOT NULL
        CHECK (amount <> 0),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_entries_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES ledger_transactions(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_entries_account
        FOREIGN KEY (account_id)
        REFERENCES accounts(id)
        ON DELETE RESTRICT
);


CREATE INDEX idx_accounts_ledger_id
    ON accounts(ledger_id);

CREATE INDEX idx_ledger_transactions_ledger_id
    ON ledger_transactions(ledger_id);

CREATE INDEX idx_entries_transaction_id
    ON entries(transaction_id);

CREATE INDEX idx_entries_account_id
    ON entries(account_id);
