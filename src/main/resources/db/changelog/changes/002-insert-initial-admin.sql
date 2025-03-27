--liquibase formatted sql

--changeset ksiepka:3 logicalFilePath:db/changelog/changes/002-insert-initial-admin.sql runOnChange:false runAlways:false failOnError:true
--comment: Insert initial administrator user required for application setup
INSERT INTO users
(login, first_name, last_name, user_type, password_hash, email, hourly_rate, created_at, updated_at)
VALUES
    (
        'admin',
        'Admin',
        'Adminowski',
        'ADMINISTRATOR',
        '$2a$10$FEHwpa3oaEBaP9SFipgS7OfguX80lqfP5HCp24eSQtiJ/DOO.CEFS',-- !!! REPLACE WITH THE HASH YOU GENERATED IN STEP 1 !!!
        'admin@example.com',
        50.00,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )

ON CONFLICT (login) DO NOTHING;

--rollback DELETE FROM users WHERE login = 'admin';