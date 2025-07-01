-- -------------------------------------- transfer ----------------------------------------------
-- Tabla principal que registra una solicitud de traslado de activos entre sedes.

CREATE TABLE IF NOT EXISTS transfer (
    id                            BIGSERIAL PRIMARY KEY,
    responsible_id                BIGINT                   NOT NULL,
    origin_headquarter_id         BIGINT                   NOT NULL,
    destination_headquarter_id    BIGINT                   NOT NULL,
    transfer_date                 TIMESTAMP WITH TIME ZONE NOT NULL,
    status                        VARCHAR(50)              NOT NULL,
    notes                         TEXT,
    created_at                    TIMESTAMP WITH TIME ZONE,
    updated_at                    TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_transfer_to_user
    FOREIGN KEY (responsible_id)
    REFERENCES toolflow_user(id),
    CONSTRAINT fk_transfer_to_origin_hq
    FOREIGN KEY (origin_headquarter_id)
    REFERENCES headquarter(id),
    CONSTRAINT fk_transfer_to_destination_hq
    FOREIGN KEY (destination_headquarter_id)
    REFERENCES headquarter(id)
    );

-- -------------------------------------- transfer_tools ----------------------------------------
-- Tabla de detalle que lista las herramientas y sus cantidades en un traslado.

CREATE TABLE IF NOT EXISTS transfer_tools (
    id           BIGSERIAL PRIMARY KEY,
    transfer_id  BIGINT  NOT NULL,
    tool_id      BIGINT  NOT NULL,
    quantity     INTEGER NOT NULL,
    CONSTRAINT fk_transfer_tools_to_transfer
    FOREIGN KEY (transfer_id)
    REFERENCES transfer(id) ON DELETE CASCADE,
    CONSTRAINT fk_transfer_tools_to_tool
    FOREIGN KEY (tool_id)
    REFERENCES tool(id)
    );

-- -------------------------------------- transfer_vehicle_parts --------------------------------
-- Tabla de detalle que lista las partes de vehículo y sus cantidades en un traslado.

CREATE TABLE IF NOT EXISTS transfer_vehicle_parts (
    id           BIGSERIAL PRIMARY KEY,
    transfer_id  BIGINT  NOT NULL,
    part_id      BIGINT  NOT NULL,
    quantity     INTEGER NOT NULL,
    CONSTRAINT fk_transfer_parts_to_transfer
    FOREIGN KEY (transfer_id)
    REFERENCES transfer(id) ON DELETE CASCADE,
    CONSTRAINT fk_transfer_parts_to_part
    FOREIGN KEY (part_id)
    REFERENCES vehicle_part(id)
    );

-- -------------------------------------- transfer_vehicles -------------------------------------
-- Tabla de detalle que lista los vehículos completos que se incluyen en un traslado.

CREATE TABLE IF NOT EXISTS transfer_vehicles (
    id           BIGSERIAL PRIMARY KEY,
    transfer_id  BIGINT NOT NULL,
    vehicle_id   BIGINT NOT NULL,
    CONSTRAINT fk_transfer_vehicles_to_transfer
    FOREIGN KEY (transfer_id)
    REFERENCES transfer(id) ON DELETE CASCADE,
    CONSTRAINT fk_transfer_vehicles_to_vehicle
    FOREIGN KEY (vehicle_id)
    REFERENCES vehicle(id)
    );