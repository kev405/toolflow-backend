package com.codeflow.toolflow.service.vehiclepart;

import com.codeflow.toolflow.dto.vehiclepart.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehiclePartService {

    /**
     * Creates a new VehiclePart and its initial inventory stock record based on the provided data.
     * The business logic determines the headquarter based on whether the part is associated
     * with a specific vehicle.
     *
     * @param request DTO containing the data for the new vehicle part and its initial quantity.
     * @return a {@link VehiclePartResponse} of the newly created part.
     */
    VehiclePartResponse createVehiclePartAndInventory(VehiclePartRequest request);

    /**
     * Associates or disassociates a part's inventory record with a specific vehicle.
     *
     * @param partId        The ID of the vehicle part.
     * @param headquarterId The ID of the headquarter where the inventory is located.
     * @param request       The DTO containing the vehicle ID to associate with, or null to disassociate.
     */
    void associateVehicle(Long partId, Long headquarterId, AssociateVehicleRequest request);


    /**
     * Updates the core details of an existing vehicle part.
     * This method does not modify inventory stock.
     *
     * @param id The ID of the vehicle part to update.
     * @param request DTO with the updated data.
     * @return the updated {@link VehiclePartResponse}.
     */
    VehiclePartResponse updateVehiclePart(Long id, VehiclePartUpdateRequest request);

    /**
     * Updates the inventory stock for a specific part at a specific headquarter.
     *
     * @param partId The ID of the vehicle part.
     * @param headquarterId The ID of the headquarter where the stock is located.
     * @param request DTO containing the new quantity.
     */
    void updateStock(Long partId, Long headquarterId, UpdateStockRequest request);

    /**
     * Retrieves a single vehicle part by its unique identifier.
     *
     * @param id the vehicle part's ID.
     * @return the corresponding {@link VehiclePartResponse}.
     */
    VehiclePartResponse getVehiclePartById(Long id);

    /**
     * Performs a logical delete on a vehicle part.
     *
     * @param id The ID of the vehicle part to delete.
     */
    void deleteVehiclePart(Long id);

    /**
     * Retrieves a paginated and filtered list of vehicle parts.
     *
     * @param name Filter by the part's unique name (can be partial match).
     * @param vehicleId Filter by the ID of the associated vehicle.
     * @param headquarterId Filter by the ID of the headquarter where the part is stocked.
     * @param pageable Pagination and sorting information.
     * @return A {@link Page} of {@link VehiclePartResponse} matching the criteria.
     */
    Page<VehiclePartResponse> getPage(String name, Long vehicleId, Long headquarterId, Pageable pageable);
}
