DROP DATABASE IF EXISTS starter_test;
DROP DATABASE IF EXISTS starter_development;
DROP DATABASE IF EXISTS starter_integration;

CREATE DATABASE starter_development;
CREATE DATABASE starter_test;

-- Ensure the user exists or create it if missing
DO $$ 
BEGIN 
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'starter') THEN
        CREATE USER starter WITH PASSWORD 'starter';
    END IF;
END $$;

-- Ensure super_test exists and has superuser privileges
DO $$ 
BEGIN 
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'super_test') THEN
        CREATE USER super_test SUPERUSER;
    END IF;
END $$;

-- Grant database access
GRANT ALL PRIVILEGES ON DATABASE starter_development TO starter;
GRANT ALL PRIVILEGES ON DATABASE starter_test TO starter;

\connect starter_development
GRANT USAGE, CREATE ON SCHEMA public TO starter;

-- Create a users table
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE users TO starter;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE users_id_seq TO starter;

\connect starter_test
GRANT USAGE, CREATE ON SCHEMA public TO starter;

-- Create a users table
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE users TO starter;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE users_id_seq TO starter;

