package com.codeflow.toolflow.persistence.vehiclepart.entity;

import com.codeflow.toolflow.persistence.vehicle.entity.Vehicle;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static jakarta.persistence.GenerationType.SEQUENCE;

/**
 * Represents a replaceable part that can be linked to a specific
 * {@link Vehicle} or kept as a generic spare.
 *
 * <p>Mapped to table {@code vehicle_part}.</p>
 */
@Entity
@Table(
        name = "vehicle_part",
        uniqueConstraints = @UniqueConstraint(name = "uk_part_name", columnNames = {"name"})
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehiclePart {

    /* ---------- Sequence ---------- */

    public static final String ID_SEQ = "vehicle_part_id_seq";

    /* ---------- Identifiers ---------- */

    @Id
    @GeneratedValue(generator = ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    /* ---------- Business fields ---------- */

    /**
     * Human-readable, system-wide unique name.
     */
    @NotNull
    @Column(nullable = false)
    private String name;

    /**
     * Vehicle type when not linked to a specific vehicle.
     */
    private String vehicleType;

    /**
     * Manufacturer/brand of the part.
     */
    @NotNull
    private String brand;

    @Builder.Default
    @OneToMany(mappedBy = "vehiclePart", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<VehiclePartInventory> inventories = new ArrayList<>();

    /**
     * Model code or reference.
     */
    private String model;

    /**
     * Logical delete.
     */
    @Column(nullable = false)
    private boolean isDeleted = false;

    /**
     * Long description of the part.
     */
    private String description;

    /**
     * Extra notes (maintenance, replacement interval, etc.).
     */
    private String notes;

    /* ---------- Audit fields ---------- */

    private Long createdBy;
    private Long updatedBy;

    @Column(updatable = false)
    private LocalDateTime createdAt;

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
