package com.codeflow.toolflow.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Data Transfer Object (DTO) representing a user request for creating or updating a vehicle.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleRequest {

    /**
     * Represents the unique identifier for the vehicle.
     */
    private Long id;

    /**
     * The category or class of the vehicle.
     * <p>
     * Examples: {@code "Car"}, {@code "Truck"}, {@code "Motorcycle"}, etc.
     * </p>
     */
    @NotNull
    private String vehicleType;

    /**
     * The license-plate number of the vehicle.
     * <p>
     * Must be unique within the fleet.
     * </p>
     */
    @NotNull
    private String plate;

    /**
     * The model designation given by the manufacturer.
     * <p>
     * Examples: {@code "F-150"}, {@code "Civic"}, {@code "Sprinter"}, etc.
     * </p>
     */
    @NotNull
    private String model;

    /**
     * The primary exterior color of the vehicle.
     * <p>
     * Optional field; can be {@code null} if not specified.
     * </p>
     */
    private String color;

    /**
     * The chassis or VIN (Vehicle Identification Number).
     * <p>
     * Uniquely identifies the vehicle body/frame.
     * </p>
     */
    @NotNull
    private String numberChasis;

    /**
     * The manufacturer or brand of the vehicle.
     * <p>
     * Examples: {@code "Toyota"}, {@code "Ford"}, {@code "BMW"}, etc.
     * </p>
     */
    @NotNull
    private String brand;

    /**
     * The current physical or assigned location of the vehicle.
     * <p>
     * This could be a depot name, city, or GPS coordinate string.
     * </p>
     */
    @NotNull
    private String location;
}
