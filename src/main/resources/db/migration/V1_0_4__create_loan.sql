-- Secuencia para la tabla 'loan'
CREATE SEQUENCE IF NOT EXISTS loan_id_seq
    START WITH 1
    INCREMENT BY 1;

-- Tabla 'loan'
CREATE TABLE IF NOT EXISTS loan
(
    id             BIGINT    NOT NULL,
    teacher_id     BIGINT    NOT NULL,
    responsible_id BIGINT,
    notes          TEXT,
    loan_status    VARCHAR(255),
    due_date       DATE      NOT NULL,
    status         BOOLEAN   NOT NULL,
    created_at     TIMESTAMP NOT NULL,
    created_by     BIGINT    NOT NULL,
    updated_at     TIMESTAMP NOT NULL,
    updated_by     BIGINT    NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (teacher_id) REFERENCES toolflow_user (id)
);

-- Secuencia para la tabla 'loan_tool'
CREATE SEQUENCE IF NOT EXISTS loan_tool_id_seq
    START WITH 1
    INCREMENT BY 1;

-- Tabla 'loan_tool'
CREATE TABLE IF NOT EXISTS loan_tool
(
    id             BIGINT  NOT NULL,
    loan_id        BIGINT  NOT NULL,
    tool_id        BIGINT  NOT NULL,
    responsible_id BIGINT,
    requested      INTEGER NOT NULL,
    loaned         INTEGER NOT NULL,
    delivered      INTEGER NOT NULL,
    damaged        INTEGER NOT NULL,
    notes          TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (loan_id) REFERENCES loan (id) ON DELETE CASCADE,
    FOREIGN KEY (tool_id) REFERENCES tool (id),
    FOREIGN KEY (responsible_id) REFERENCES toolflow_user (id)
);
