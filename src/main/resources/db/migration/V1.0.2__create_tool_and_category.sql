-- -------------------------------------- category -----------------------------------------------

CREATE TABLE IF NOT EXISTS tool_category
(
    id         BIGINT       NOT NULL,
    name       VARCHAR(255) NOT NULL,
    status     BOOLEAN      NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    created_by BIGINT       NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    updated_by BIGINT       NOT NULL,
    PRIMARY KEY (id)
    );

CREATE SEQUENCE IF NOT EXISTS tool_category_id_seq START WITH 1 INCREMENT BY 1;

-- -------------------------------------- tool -----------------------------------------------

CREATE TABLE IF NOT EXISTS tool
(
    id                   BIGINT       NOT NULL,
    tool_name            VARCHAR(350) NOT NULL,
    brand                VARCHAR(255) NOT NULL,
    quantity             INTEGER      NOT NULL,
    available            INTEGER,
    damaged              INTEGER,
    on_loan              INTEGER,
    notes                TEXT,
    consumable           BOOLEAN,
    minimal_registration INTEGER,
    status               BOOLEAN      NOT NULL,
    created_at           TIMESTAMP    NOT NULL,
    created_by           BIGINT       NOT NULL,
    updated_at           TIMESTAMP    NOT NULL,
    updated_by           BIGINT       NOT NULL,
    category             BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (category) REFERENCES tool_category (id)
    );

CREATE SEQUENCE IF NOT EXISTS tool_id_seq START WITH 1 INCREMENT BY 1;
