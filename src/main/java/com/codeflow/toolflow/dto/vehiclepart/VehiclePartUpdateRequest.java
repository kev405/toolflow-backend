package com.codeflow.toolflow.dto.vehiclepart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * DTO for updating an existing VehiclePart.
 * It does not include the 'quantity' field, as stock is managed separately.
 */
@Data
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehiclePartUpdateRequest {

    @NotBlank(message = "Part name cannot be blank")
    private String name;

    private String vehicleType;

    @NotBlank(message = "Brand cannot be blank")
    private String brand;

    private String model;
    private String description;
    private String notes;

    private Integer vehicleId;
}
