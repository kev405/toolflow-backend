-- Secuencia para la tabla 'headquarter'
CREATE SEQUENCE IF NOT EXISTS headquarter_id_seq
    START WITH 1
    INCREMENT BY 1;

-- Tabla 'headquarter'
CREATE TABLE IF NOT EXISTS headquarter
(
    id             BIGINT       NOT NULL,
    name           VARCHAR(150) NOT NULL,
    address        VARCHAR(255) NOT NULL,
    main           BOOLEAN      NOT NULL,
    status         BOOLEAN      NOT NULL,
    responsible_id BIGINT       ,
    created_at     TIMESTAMP    NOT NULL,
    created_by     BIGINT       NOT NULL,
    updated_at     TIMESTAMP    NOT NULL,
    updated_by     BIGINT       NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (responsible_id) REFERENCES toolflow_user (id)
    );

-- Inserción de la sede principal (sin responsable asignado)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM headquarter WHERE main = TRUE) THEN
        INSERT INTO headquarter (
            id, name, address, main, status,
            responsible_id, created_at, created_by,
            updated_at, updated_by
        )
        VALUES (
            nextval('headquarter_id_seq'),
            'Sede Principal',
            'Calle 44 #10-24',
            TRUE,
            TRUE,
            NULL,  -- Sin responsable asignado por ahora
            CURRENT_TIMESTAMP,
            1,
            CURRENT_TIMESTAMP,
            1
        );
END IF;
END $$;

