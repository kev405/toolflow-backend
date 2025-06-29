package com.codeflow.toolflow.dto.vehiclepart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating the stock quantity of a VehiclePartInventory record.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateStockRequest {

    /**
     * The new stock quantity. Must be a non-negative integer.
     */
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity must not be less than 0")
    private Integer quantity;

}
