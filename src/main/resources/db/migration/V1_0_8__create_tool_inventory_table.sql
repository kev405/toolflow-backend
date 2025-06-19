-- ------------------------------ tool_inventory --------------------------------------

CREATE TABLE IF NOT EXISTS tool_inventory (
                                              id            BIGINT       NOT NULL PRIMARY KEY,
                                              tool_id       BIGINT       NOT NULL,
                                              headquarter_id BIGINT      NOT NULL,
                                              quantity      INTEGER      NOT NULL,
                                              available     INTEGER      NOT NULL,
                                              damaged       INTEGER      NOT NULL,
                                              on_loan       INTEGER      NOT NULL,
                                              created_at    TIMESTAMP    NOT NULL,
                                              created_by    BIGINT       NOT NULL,
                                              updated_at    TIMESTAMP    NOT NULL,
                                              updated_by    BIGINT       NOT NULL,

                                              CONSTRAINT fk_tool_inventory_tool FOREIGN KEY (tool_id) REFERENCES tool (id),
    CONSTRAINT fk_tool_inventory_headquarter FOREIGN KEY (headquarter_id) REFERENCES headquarter (id)
    );

-- Crea una secuencia para IDs si estás usando PostgreSQL
CREATE SEQUENCE IF NOT EXISTS tool_inventory_id_seq START WITH 1 INCREMENT BY 1;

-- Índice para búsquedas frecuentes
CREATE INDEX IF NOT EXISTS idx_tool_inventory_tool_id ON tool_inventory (tool_id);
CREATE INDEX IF NOT EXISTS idx_tool_inventory_headquarter_id ON tool_inventory (headquarter_id);
