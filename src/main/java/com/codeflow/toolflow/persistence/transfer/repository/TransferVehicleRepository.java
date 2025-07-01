package com.codeflow.toolflow.persistence.transfer.repository;

import com.codeflow.toolflow.persistence.transfer.entity.TransferVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link TransferVehicle} entities.
 */
@Repository
public interface TransferVehicleRepository extends JpaRepository<TransferVehicle, Long> {
}
