package com.codeflow.toolflow.persistence.vehiclepart.entity;

import com.codeflow.toolflow.persistence.headquarter.entity.Headquarter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static jakarta.persistence.GenerationType.SEQUENCE;

/**
 * Represents the stock of a specific vehicle part held in a given head-
 * quarter’s warehouse.
 *
 * <p>The entity is mapped to the {@code part_inventory} table.</p>
 */
@Entity
@Table(name = "vehicle_part_inventory")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehiclePartInventory {

    /* ---------- Sequence ---------- */

    /**
     * Database sequence that generates primary keys for {@code PartInventory}.
     */
    public static final String ID_SEQ = "part_inventory_id_seq";

    /* ---------- Identifiers ---------- */

    /**
     * Unique identifier for the inventory record.
     */
    @Id
    @GeneratedValue(generator = ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    /* ---------- Relationships ---------- */

    /**
     * Part being stocked.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "vehicle_part_id", referencedColumnName = "id", nullable = false)
    @NotNull
    private VehiclePart vehiclePart;

    /**
     * Headquarter/warehouse where the part is stored.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "headquarter_id", referencedColumnName = "id", nullable = false)
    @NotNull
    private Headquarter headquarter;

    /**
     * Vehicle to which the part belongs when {@code vehicleAssociated = true}.
     * Optional; must be non‐null only in that case.
     */
    private Long vehicle;

    /* ---------- Business fields ---------- */

    /**
     * Part name duplicated for reporting convenience.
     * <p>Copied from {@code vehiclePart.getName()} on creation.</p>
     */
    private String name;

    /**
     * Indicates whether the part record was created from
     * a vehicle association (true) or registered directly (false).
     */
    private Boolean vehicleAssociated;

    /**
     * Quantity of items in stock (must be ≥ 0).
     */
    @NotNull
    @Min(0)
    private Integer quantity;

    /* ---------- Audit fields ---------- */

    /** User ID that created the record. */
    private Long createdBy;

    /** Timestamp when the record was created (set automatically). */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** User ID that last modified the record. */
    private Long updatedBy;

    /** Timestamp of the last modification (updated automatically). */
    private LocalDateTime updatedAt;

    /* ---------- Lifecycle callbacks ---------- */

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
