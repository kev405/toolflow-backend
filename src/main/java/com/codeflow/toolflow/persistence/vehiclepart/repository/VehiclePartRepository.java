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

    Optional<VehiclePart> findByNameAndIsDeletedFalse(String name);

    /**
     * Finds a non-deleted part by its ID, explicitly fetching all related
     * inventory and headquarter data in a single query to prevent lazy loading issues.
     * @param id The ID of the part.
     * @return An Optional containing the fully initialized part if found and not deleted.
     */
    @Query("SELECT vp FROM VehiclePart vp " +
            "LEFT JOIN FETCH vp.inventories inv " +
            "LEFT JOIN FETCH inv.headquarter " +
            "WHERE vp.id = :id AND vp.isDeleted = false")
    Optional<VehiclePart> findByIdAndIsDeletedFalse(@Param("id") Long id);

    /**
     * Checks if a non-deleted vehicle part with the given name and association status already exists.
     * This is used to enforce the composite unique constraint before attempting to save a new entity.
     *
     * @param name The name of the vehicle part.
     * @return true if a matching part exists, false otherwise.
     */
    boolean existsByNameAndIsDeletedFalse(String name);

    /**
     * Finds a paginated list of non-deleted vehicle parts based on optional filter criteria.
     * This query joins with the inventory to filter by vehicle and headquarter.
     *
     * @param namePattern Optional filter for the part's name (case-insensitive, partial match).
     * @param vehicleId Optional filter for the associated vehicle's ID.
     * @param headquarterId Optional filter for the headquarter's ID where the part is stocked.
     * @param pageable Pagination and sorting information.
     * @return A page of distinct {@link VehiclePart} entities matching the criteria.
     */
    @Query("SELECT DISTINCT vp FROM VehiclePart vp " +
            "LEFT JOIN VehiclePartInventory vpi ON vpi.vehiclePart.id = vp.id " +
            "WHERE vp.isDeleted = false " +
            "AND (:namePattern IS NULL OR LOWER(vp.name) LIKE :namePattern) " +
            "AND (:vehicleId IS NULL OR vpi.vehicle = :vehicleId) " +
            "AND (:headquarterId IS NULL OR vpi.headquarter.id = :headquarterId)" +
            "AND (:model IS NULL OR LOWER(vp.model) LIKE :model) " +
            "AND (:brand IS NULL OR LOWER(vp.brand) LIKE :brand) ")
    Page<VehiclePart> findWithFilters(
            @Param("namePattern") String namePattern,
            @Param("model") String Model,
            @Param("brand") String Brand,
            @Param("vehicleId") Long vehicleId,
            @Param("headquarterId") Long headquarterId,
            Pageable pageable);
}