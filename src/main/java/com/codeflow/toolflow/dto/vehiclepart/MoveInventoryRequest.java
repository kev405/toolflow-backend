package com.codeflow.toolflow.dto.vehiclepart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for moving a quantity of a vehicle part from one inventory association to another
 * within the same headquarter.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoveInventoryRequest {

    /**
     * The ID of the vehicle from which the parts are being moved.
     * Use null to represent the generic (unassociated) stock.
     */
    private Long sourceVehicleId;

    /**
     * The ID of the vehicle to which the parts are being moved.
     * Use null to represent the generic (unassociated) stock.
     */
    private Long destinationVehicleId;

    /**
     * The quantity of parts to move. Must be at least 1.
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity to move must be at least 1")
    private Integer quantity;
}