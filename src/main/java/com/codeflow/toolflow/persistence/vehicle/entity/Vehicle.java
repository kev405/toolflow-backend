package com.codeflow.toolflow.persistence.vehicle.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static jakarta.persistence.GenerationType.SEQUENCE;

/**
 * Represents a vehicle in the system’s fleet or asset registry.
 * <p>
 * Each {@code Vehicle} instance stores essential information such as the
 * type of vehicle, license‐plate number, model, color, chassis number,
 * brand, and current location.
 * </p>
 * <p>
 * The entity is mapped to the database table {@code vehicle}.
 * </p>
 */
@Entity
@Table(name = "vehicle")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vehicle {

    /**
     * The sequence name used for the {@code id} field in the {@code Vehicle} entity.
     * Defines the database sequence {@code vehicle_id_seq} responsible for generating
     * unique identifiers for vehicles.
     */
    public static final String ID_SEQ = "vehicle_id_seq";

    /**
     * Represents the unique identifier for the vehicle.
     * <p>
     * Values are generated automatically using the {@code vehicle_id_seq} sequence.
     * </p>
     */
    @Id
    @GeneratedValue(generator = ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
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
