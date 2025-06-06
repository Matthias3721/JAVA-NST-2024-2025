-- Tabela 'reminders'
CREATE TABLE IF NOT EXISTS reminders (
    id SERIAL PRIMARY KEY,
    presc_id INTEGER NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    remind_time TIMESTAMP NOT NULL,
    channel VARCHAR(10) NOT NULL,
    sent BOOLEAN NOT NULL
);

-- Tabela 'stock'
CREATE TABLE IF NOT EXISTS stock (
    id SERIAL PRIMARY KEY,
    med_id INTEGER NOT NULL UNIQUE REFERENCES medications(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL,
    threshold INTEGER NOT NULL
);
