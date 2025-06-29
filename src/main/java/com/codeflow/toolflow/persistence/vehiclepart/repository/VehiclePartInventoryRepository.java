package com.codeflow.toolflow.persistence.vehiclepart.repository;

import com.codeflow.toolflow.persistence.vehiclepart.entity.VehiclePartInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link VehiclePartInventory} entities.
 */
@Repository
public interface VehiclePartInventoryRepository extends JpaRepository<VehiclePartInventory, Long> {


    /**
     * Finds an inventory record by the ID of the vehicle part and the ID of the headquarter.
     *
     * @param vehiclePartId The ID of the vehicle part.
     * @param headquarterId The ID of the headquarter.
     * @return An {@link Optional} containing the found inventory record, or empty if not found.
     */
    Optional<VehiclePartInventory> findByVehiclePartIdAndHeadquarterId(Long vehiclePartId, Long headquarterId);

    /**
     * Finds all inventory records associated with a specific vehicle part.
     *
     * @param vehiclePartId The ID of the vehicle part.
     * @return A list of all inventory records for that part.
     */
    List<VehiclePartInventory> findAllByVehiclePartId(Long vehiclePartId);

}
