package com.codeflow.toolflow.dto.vehiclepart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para asociar o desasociar un registro de inventario con un vehículo.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssociateVehicleRequest {

    /**
     * El ID del vehículo al cual asociar el inventario.
     * Si es null, el inventario se desasociará de cualquier vehículo.
     */
    private Long vehicleId;
}