----------------------------- toolflow_user -----------------------------

CREATE TABLE if not exists toolflow_user (
    id BIGINT NOT NULL,
    username VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone BIGINT,
    email VARCHAR(255) NOT NULL,
    status boolean NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    updated_by BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (username)
);

CREATE SEQUENCE if not exists toolflow_user_id_seq START WITH 1 INCREMENT BY 1;

-- -------------------------------------- user_role -----------------------------------------------

CREATE TABLE if not exists user_role (
    id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES toolflow_user(id)
);

CREATE SEQUENCE if not exists user_role_id_seq START WITH 1 INCREMENT BY 1;