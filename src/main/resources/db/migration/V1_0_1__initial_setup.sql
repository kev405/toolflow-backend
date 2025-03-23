----------------------------- toolflow_user -----------------------------

CREATE TABLE if not exists toolflow_user (
    id BIGINT NOT NULL,
    username VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    PRIMARY KEY (id),
    UNIQUE (username)
);

CREATE SEQUENCE if not exists toolflow_user_id_seq START WITH 1 INCREMENT BY 1;

-- -------------------------------------- category -----------------------------------------------

CREATE TABLE if not exists category (
    id BIGINT NOT NULL,
    name VARCHAR(255) DEFAULT NULL,
    status VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE SEQUENCE if not exists category_id_seq START WITH 1 INCREMENT BY 1;

-- -------------------------------------- product -----------------------------------------------

CREATE TABLE if not exists product (
    id BIGINT NOT NULL,
    name VARCHAR(255) DEFAULT NULL,
    price DECIMAL(10,2) DEFAULT NULL,
    status VARCHAR(50) DEFAULT NULL,
    category_id BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE SEQUENCE if not exists product_id_seq START WITH 1 INCREMENT BY 1;