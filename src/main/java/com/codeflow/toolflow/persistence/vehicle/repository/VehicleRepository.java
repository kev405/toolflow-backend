package com.codeflow.toolflow.persistence.vehicle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.codeflow.toolflow.persistence.vehicle.entity.Vehicle;


@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT e " + "FROM Vehicle e " + "WHERE (:vehicleType is null OR e.vehicleType = :vehicleType) "
            + "AND (:plate is null OR e.plate = :plate) "
            + "AND (:model is null OR e.model = :model) "
            + "AND (:color is null OR e.color = :color) "
            + "AND (:numberChasis is null OR e.numberChasis = :numberChasis) "
            + "AND (:brand is null OR e.brand = :brand) "
            + "AND (:location is null OR e.location = :location) ")
    Page<Vehicle> queryPageable(@Param("vehicleType") String vehicleType, @Param("plate") String plate,
                                @Param("model") String model, @Param("color") String color,
                                @Param("numberChasis") String numberChasis, @Param("brand") String brand,
                                @Param("location") String location, Pageable page);

}
