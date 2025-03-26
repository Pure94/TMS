--liquibase formatted sql

--changeset ksiepka:1 logicalFilePath:db/changelog/changes/001-create-users-table.sql
--comment: Create the initial users table
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       login VARCHAR(50) NOT NULL UNIQUE,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       user_type VARCHAR(20) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       hourly_rate NUMERIC(10, 2) NOT NULL CHECK (hourly_rate > 0.00),
                       created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
--rollback DROP TABLE users;

--changeset ksiepka:2 logicalFilePath:db/changelog/changes/001-create-users-table.sql
--comment: Add index on last_name for users table
CREATE INDEX idx_user_last_name ON users (last_name);
--rollback DROP INDEX idx_user_last_name;