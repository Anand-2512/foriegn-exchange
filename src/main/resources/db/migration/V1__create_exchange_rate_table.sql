CREATE TABLE exchange_rate (
    id SERIAL PRIMARY KEY,
    date DATE NOT NULL,
    source_currency VARCHAR(3) NOT NULL,
    target_currency VARCHAR(3) NOT NULL,
    exchange_rate DOUBLE PRECISION NOT NULL
);