--liquibase formatted sql

--changeset ksiepka:7 logicalFilePath:db/changelog/changes/004-create-time-entries-table.sql
CREATE TABLE time_entries (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              project_id UUID NOT NULL,
                              user_id UUID NOT NULL,
                              start_time TIMESTAMPTZ NOT NULL,
                              end_time TIMESTAMPTZ NOT NULL,
                              description TEXT NULL,
                              created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT fk_time_entries_project
                                  FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,

                              CONSTRAINT fk_time_entries_user
                                  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

                              CONSTRAINT time_entries_time_check CHECK (end_time > start_time)
);
--rollback DROP TABLE time_entries;

--changeset ksiepka:8 logicalFilePath:db/changelog/changes/004-create-time-entries-table.sql
CREATE INDEX idx_time_entries_project_id ON time_entries (project_id);
CREATE INDEX idx_time_entries_user_id ON time_entries (user_id);
CREATE INDEX idx_time_entries_start_time ON time_entries (start_time);
--rollback DROP INDEX idx_time_entries_project_id;
--rollback DROP INDEX idx_time_entries_user_id;
--rollback DROP INDEX idx_time_entries_start_time;