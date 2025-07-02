package com.codeflow.toolflow.persistence.vehiclepart.repository;

import com.codeflow.toolflow.persistence.vehiclepart.entity.VehiclePart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link VehiclePart} entities.
 */
@Repository
public interface VehiclePartRepository extends JpaRepository<VehiclePart, Long> {

    /**
     * Finds a non-deleted part by its ID.
     * @param id The ID of the part.
     * @return An Optional containing the part if found and not deleted.
     */
    Optional<VehiclePart> findByIdAndIsDeletedFalse(Long id);

    /**
     * Finds a paginated list of non-deleted vehicle parts based on optional filter criteria.
     * This query joins with the inventory to filter by vehicle and headquarter.
     *
     * @param name Optional filter for the part's name (case-insensitive, partial match).
     * @param vehicleId Optional filter for the associated vehicle's ID.
     * @param headquarterId Optional filter for the headquarter's ID where the part is stocked.
     * @param pageable Pagination and sorting information.
     * @return A page of distinct {@link VehiclePart} entities matching the criteria.
     */
    @Query("SELECT DISTINCT vp FROM VehiclePart vp " +
            "LEFT JOIN VehiclePartInventory vpi ON vpi.vehiclePart.id = vp.id " +
            "WHERE vp.isDeleted = false " +
            "AND (:namePattern IS NULL OR LOWER(vp.name) LIKE :namePattern) " + // <-- CAMBIO CLAVE
            "AND (:vehicleId IS NULL OR vpi.vehicle = :vehicleId) " +
            "AND (:headquarterId IS NULL OR vpi.headquarter.id = :headquarterId)")
    Page<VehiclePart> findWithFilters(
            @Param("namePattern") String namePattern, // Renombrado para mayor claridad
            @Param("vehicleId") Long vehicleId,
            @Param("headquarterId") Long headquarterId,
            Pageable pageable);
}