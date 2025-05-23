
         -- Create a users table
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       finance BOOLEAN DEFAULT FALSE,
                       sports BOOLEAN DEFAULT FALSE,
                       fashion BOOLEAN DEFAULT FALSE,
                       technology BOOLEAN DEFAULT FALSE,
                       politics BOOLEAN DEFAULT FALSE
);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE users TO starter;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE users_id_seq TO starter;

