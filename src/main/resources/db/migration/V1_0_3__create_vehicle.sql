-- -------------------------------------- vehicle -----------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle (
    id             BIGINT       NOT NULL,
    vehicle_type   VARCHAR(255) NOT NULL,
    plate          VARCHAR(255) NOT NULL,
    model          VARCHAR(255) NOT NULL,
    color          VARCHAR(255),
    number_chasis  VARCHAR(255) NOT NULL,
    brand          VARCHAR(255) NOT NULL,
    location       VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (plate)
    );

CREATE SEQUENCE IF NOT EXISTS vehicle_id_seq START WITH 1 INCREMENT BY 1;