package com.codeflow.toolflow.persistence.transfer.entity;

import com.codeflow.toolflow.persistence.vehicle.entity.Vehicle;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "transfer_vehicles")
@Data
public class TransferVehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transfer_id", nullable = false)
    private Transfer transfer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
}
