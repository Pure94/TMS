--liquibase formatted sql

--changeset ksiepka:4 logicalFilePath:db/changelog/changes/003-create-projects-tables.sql
--comment: Create the projects table
CREATE TABLE projects (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name VARCHAR(255) NOT NULL UNIQUE,
                          description TEXT NULL,
                          start_date DATE NOT NULL,
                          end_date DATE NOT NULL,
                          budget NUMERIC(19, 2) NOT NULL,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT projects_budget_check CHECK (budget > 0.00),
                          CONSTRAINT projects_date_check CHECK (end_date >= start_date)
);
--rollback DROP TABLE projects;

--changeset ksiepka:5 logicalFilePath:db/changelog/changes/003-create-projects-tables.sql
--comment: Create the project_assignments join table for User-Project relationship
CREATE TABLE project_assignments (
                                     project_id UUID NOT NULL,
                                     user_id UUID NOT NULL,

                                     CONSTRAINT pk_project_assignments PRIMARY KEY (project_id, user_id),

                                     CONSTRAINT fk_project_assignments_project
                                         FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,

                                     CONSTRAINT fk_project_assignments_user
                                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
--rollback DROP TABLE project_assignments;

--changeset ksiepka:6 logicalFilePath:db/changelog/changes/003-create-projects-tables.sql
--comment: Add indexes to foreign keys in project_assignments for performance
CREATE INDEX idx_project_assignments_project_id ON project_assignments (project_id);
CREATE INDEX idx_project_assignments_user_id ON project_assignments (user_id);
--rollback DROP INDEX idx_project_assignments_project_id;
--rollback DROP INDEX idx_project_assignments_user_id;