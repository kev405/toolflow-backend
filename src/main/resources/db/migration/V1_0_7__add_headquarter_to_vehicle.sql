-- Agregar columna para la sede
ALTER TABLE vehicle
    ADD COLUMN headquarter_id BIGINT;

-- Establecer la clave foránea hacia la tabla headquarter
ALTER TABLE vehicle
    ADD CONSTRAINT fk_vehicle_headquarter
        FOREIGN KEY (headquarter_id)
            REFERENCES headquarter(id);

ALTER TABLE vehicle ALTER COLUMN headquarter_id SET NOT NULL;
