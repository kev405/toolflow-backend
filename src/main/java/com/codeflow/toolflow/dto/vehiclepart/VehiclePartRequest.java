package com.codeflow.toolflow.dto.vehiclepart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Data Transfer Object for creating or updating a VehiclePart.
 * It carries all the necessary information from the client to the server.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehiclePartRequest {

    /**
     * The unique name for the vehicle part. Cannot be null or blank.
     */
    @NotBlank(message = "Part name cannot be blank")
    private String name;

    /**
     * The type of vehicle this part is intended for (e.g., "Car", "Motorcycle").
     * Required if vehicleId is not provided.
     */
    private String vehicleType;

    /**
     * The manufacturer or brand of the part. Cannot be null.
     */
    @NotBlank(message = "Brand cannot be blank")
    private String brand;

    /**
     * Indicates whether the part record was created from
     * a vehicle association (true) or registered directly (false).
     */
    @NotNull(message = "vehicleAssociated is required")
    private Boolean vehicleAssociated = false;

    /**
     * The model identifier of the part. Optional.
     */
    private String model;

    /**
     * A detailed description of the part. Optional.
     */
    private String description;

    /**
     * Any additional notes or maintenance instructions. Optional.
     */
    private String notes;

    /**
     * The ID of the specific vehicle this part is associated with.
     * If provided, vehicleType is not required.
     */
    private Long vehicleId;

    /**
     * The initial quantity for the inventory record. Must be a non-negative integer.
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be a non-negative number")
    private Integer quantity;
}
