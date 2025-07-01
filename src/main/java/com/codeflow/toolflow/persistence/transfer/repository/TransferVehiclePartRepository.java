package com.codeflow.toolflow.persistence.transfer.repository;

import com.codeflow.toolflow.persistence.transfer.entity.TransferVehiclePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link TransferVehiclePart} entities.
 */
@Repository
public interface TransferVehiclePartRepository extends JpaRepository<TransferVehiclePart, Long> {
}
