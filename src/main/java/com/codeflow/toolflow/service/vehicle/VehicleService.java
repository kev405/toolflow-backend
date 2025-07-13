package com.codeflow.toolflow.service.vehicle;

import com.codeflow.toolflow.dto.vehicle.TransferableVehicleResponse;
import com.codeflow.toolflow.dto.vehicle.VehicleRequest;
import com.codeflow.toolflow.dto.vehicle.VehicleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehicleService {

    /**
     * Registers a new Vehicle in the system.
     *
     * @param VehicleRequest DTO with Vehicle data.
     * @return the created VehicleResponse.
     */
    VehicleResponse registerOneVehicle(VehicleRequest VehicleRequest);

    /**
     * Updates an existing Vehicle.
     *
     * @param VehicleRequest updated data.
     * @return updated VehicleResponse.
     */
    VehicleResponse updateOneVehicle(VehicleRequest VehicleRequest);

    /**
     * Retrieves a single Vehicle by ID.
     *
     * @param id the Vehicle's ID.
     * @return VehicleResponse.
     */
    VehicleResponse getOne(Long id);

    /**
     * Deletes (soft delete) a Vehicle.
     *
     * @param id ID of the Vehicle to delete.
     */
    void deleteOneVehicle(Long id);

    /**
     * Retrieves a paginated list of vehicles filtered by the supplied criteria.
     * <p>
     * Every filter parameter is optional—any {@code null} value is ignored.
     * Pagination and sorting follow Spring Data conventions.
     * </p>
     *
     * @param vehicleType   vehicle class/category (e.g. {@code "Car"}, {@code "Truck"}); may be {@code null}
     * @param plate         license-plate number; may be {@code null}
     * @param model         model designation; may be {@code null}
     * @param color         exterior color; may be {@code null}
     * @param numberChasis  chassis / VIN number; may be {@code null}
     * @param brand         manufacturer brand; may be {@code null}
     * @param location      current location of the vehicle; may be {@code null}
     * @param headquarterId ID of the headquarter where the vehicle is registered; may be {@code null}
     * @param pageable      pagination and sorting configuration
     * @return a {@link Page} of {@link VehicleResponse} instances that satisfy the filters
     */
    Page<VehicleResponse> getPage(String vehicleType, String plate, String model, String color,
                                  String numberChasis, String brand, String location, Long headquarterId, Pageable pageable);

    /**
     * Retrieves all vehicles associated with a specific headquarter.
     */
    List<TransferableVehicleResponse> getAvailableVehicles(Long headquarterId);

    /**
     * Retrieves all vehicles.
     */
    List<TransferableVehicleResponse> getAllVehicles();

}
