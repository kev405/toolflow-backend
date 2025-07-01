package com.codeflow.toolflow.persistence.transfer.entity;

import com.codeflow.toolflow.persistence.vehiclepart.entity.VehiclePart;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "transfer_vehicle_parts")
@Data
public class TransferVehiclePart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transfer_id", nullable = false)
    private Transfer transfer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_part_id", nullable = false)
    private VehiclePart vehiclePart;

    @Column(nullable = false)
    private Integer quantity;
}
