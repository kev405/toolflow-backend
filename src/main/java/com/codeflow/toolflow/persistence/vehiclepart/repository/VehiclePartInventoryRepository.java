package com.codeflow.toolflow.persistence.vehiclepart.repository;

import com.codeflow.toolflow.persistence.vehiclepart.entity.VehiclePartInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link VehiclePartInventory} entities.
 */
@Repository
public interface VehiclePartInventoryRepository extends JpaRepository<VehiclePartInventory, Long> {

    /**
     * Finds a specific inventory record for a part in a headquarter that is associated with a specific vehicle.
     *
     * @param vehiclePartId The ID of the vehicle part.
     * @param headquarterId The ID of the headquarter.
     * @param vehicleId The ID of the associated vehicle.
     * @return An Optional containing the inventory record if found.
     */
    Optional<VehiclePartInventory> findByVehiclePartIdAndHeadquarterIdAndVehicle(Long vehiclePartId, Long headquarterId, Long vehicleId);

    /**
     * Finds the generic (unassociated) inventory record for a part in a headquarter.
     *
     * @param vehiclePartId The ID of the vehicle part.
     * @param headquarterId The ID of the headquarter.
     * @return An Optional containing the generic inventory record if found.
     */
    Optional<VehiclePartInventory> findByVehiclePartIdAndHeadquarterIdAndVehicleIsNull(Long vehiclePartId, Long headquarterId);

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

    /**
     * Finds all inventory records associated with a specific vehicle.
     *
     * @param vehicleId The ID of the vehicle.
     * @return A list of all inventory records for that vehicle.
     */
    List<VehiclePartInventory> findAllByVehicle(Long vehicleId);

    /**
     * Finds all inventory for parts that are not associated with a specific vehicle
     * and have available stock at a given headquarter.
     * @param headquarterId The ID of the headquarter.
     * @return A list of vehicle part inventory records.
     */
    @Query("SELECT vpi FROM VehiclePartInventory vpi " +
            "WHERE vpi.headquarter.id = :headquarterId " +
            "AND vpi.quantity > 0 " +
            "AND vpi.vehiclePart.isDeleted = false " +
            "AND vpi.vehicleAssociated = false")
    List<VehiclePartInventory> findAvailableNonAssociatedPartsByHeadquarter(@Param("headquarterId") Long headquarterId);

    boolean existsByVehiclePartIdAndVehicle(Long vehiclePartId, Long vehicleId);
}
