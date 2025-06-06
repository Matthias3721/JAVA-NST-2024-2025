-- Tabela 'medications'
CREATE TABLE IF NOT EXISTS medications (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    substance VARCHAR(100) NOT NULL,
    unit VARCHAR(20),
    manufacturer VARCHAR(100)
);

-- Tabela 'prescriptions'
CREATE TABLE IF NOT EXISTS prescriptions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    med_id INTEGER NOT NULL REFERENCES medications(id) ON DELETE CASCADE,
    dose VARCHAR(50) NOT NULL,
    frequency VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL
);
