-- -------------------------------------- vehicle_part ------------------------------------------
-- Esta tabla almacena información sobre partes y repuestos de vehículos,
-- que son componentes genéricos no ligados directamente a un vehículo específico.

CREATE TABLE IF NOT EXISTS vehicle_part (
    id                    BIGINT          NOT NULL,
    name                  VARCHAR(255)    NOT NULL,
    vehicle_type          VARCHAR(255),
    vehicle_associated    BOOLEAN,
    brand                 VARCHAR(255)    NOT NULL,
    model                 VARCHAR(255),
    description           VARCHAR(255),
    notes                 VARCHAR(255),
    is_deleted            BOOLEAN,
    created_by            BIGINT,
    created_at            TIMESTAMP,
    updated_by            BIGINT,
    updated_at            TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_part_name_associated UNIQUE (name, vehicle_associated)
    );

CREATE SEQUENCE IF NOT EXISTS vehicle_part_id_seq START WITH 1 INCREMENT BY 1;

-- -------------------------------------- part_inventory ----------------------------------------
-- Esta tabla hace un seguimiento del stock de partes de vehículos en cada sede.
-- Puede opcionalmente vincular un registro de inventario a un vehículo específico.

CREATE TABLE IF NOT EXISTS vehicle_part_inventory (
    id                  BIGINT          NOT NULL,
    vehicle_part_id     BIGINT          NOT NULL,
    headquarter_id      BIGINT          NOT NULL,
    vehicle             BIGINT, -- ID del vehículo, no es FK para permitir nulos si no está asociado
    name                VARCHAR(255),
    vehicle_associated  BOOLEAN,
    quantity            INTEGER         NOT NULL,
    created_by          BIGINT,
    created_at          TIMESTAMP,
    updated_by          BIGINT,
    updated_at          TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_inventory_to_vehicle_part
    FOREIGN KEY (vehicle_part_id)
    REFERENCES vehicle_part(id),
    CONSTRAINT fk_inventory_to_headquarter
    FOREIGN KEY (headquarter_id)
    REFERENCES headquarter(id)
    );

CREATE SEQUENCE IF NOT EXISTS part_inventory_id_seq START WITH 1 INCREMENT BY 1;
