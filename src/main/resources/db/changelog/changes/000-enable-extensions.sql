--liquibase formatted sql

--changeset ksiepka:0 logicalFilePath:db/changelog/changes/000-enable-extensions.sql
--comment: Enable pgcrypto extension for UUID generation
CREATE EXTENSION IF NOT EXISTS pgcrypto;

--rollback DROP EXTENSION IF EXISTS pgcrypto;