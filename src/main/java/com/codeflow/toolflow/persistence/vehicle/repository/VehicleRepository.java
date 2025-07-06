package com.codeflow.toolflow.persistence.vehicle.repository;

import java.util.List;
import com.codeflow.toolflow.persistence.vehicle.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("""
    SELECT v FROM Vehicle v
    WHERE (LOWER(v.vehicleType) LIKE CONCAT('%', LOWER(:vehicleType), '%') OR :vehicleType IS NULL)
      AND (LOWER(v.plate) LIKE CONCAT('%', LOWER(:plate), '%') OR :plate IS NULL)
      AND (LOWER(v.model) LIKE CONCAT('%', LOWER(:model), '%') OR :model IS NULL)
      AND (LOWER(v.color) LIKE CONCAT('%', LOWER(:color), '%') OR :color IS NULL)
      AND (LOWER(v.numberChasis) LIKE CONCAT('%', LOWER(:numberChasis), '%') OR :numberChasis IS NULL)
      AND (LOWER(v.brand) LIKE CONCAT('%', LOWER(:brand), '%') OR :brand IS NULL)
      AND (LOWER(v.location) LIKE CONCAT('%', LOWER(:location), '%') OR :location IS NULL)
      AND (:headquarterId IS NULL OR v.headquarter.id = :headquarterId)
""")
    Page<Vehicle> queryPageable(
            @Param("vehicleType") String vehicleType,
            @Param("plate") String plate,
            @Param("model") String model,
            @Param("color") String color,
            @Param("numberChasis") String numberChasis,
            @Param("brand") String brand,
            @Param("location") String location,
            @Param("headquarterId") Long headquarterId,
            Pageable pageable
    );

    /**
     * Finds all vehicles assigned to a specific headquarter.
     * @param headquarterId The ID of the headquarter.
     * @return A list of vehicles.
     */
    List<Vehicle> findAllByHeadquarterId(Long headquarterId);

}
