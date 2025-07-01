package com.codeflow.toolflow.service.vehiclepart.Impl;

import com.codeflow.toolflow.dto.vehiclepart.UpdateStockRequest;
import com.codeflow.toolflow.dto.vehiclepart.VehiclePartRequest;
import com.codeflow.toolflow.dto.vehiclepart.VehiclePartResponse;
import com.codeflow.toolflow.mapper.vehiclepart.VehiclePartMapper;
import com.codeflow.toolflow.persistence.headquarter.entity.Headquarter;
import com.codeflow.toolflow.persistence.vehicle.entity.Vehicle;
import com.codeflow.toolflow.persistence.vehicle.repository.VehicleRepository;
import com.codeflow.toolflow.persistence.vehiclepart.entity.VehiclePart;
import com.codeflow.toolflow.persistence.vehiclepart.entity.VehiclePartInventory;
import com.codeflow.toolflow.persistence.vehiclepart.repository.VehiclePartInventoryRepository;
import com.codeflow.toolflow.persistence.vehiclepart.repository.VehiclePartRepository;
import com.codeflow.toolflow.service.headquarter.HeadquarterService;
import com.codeflow.toolflow.service.vehiclepart.VehiclePartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of {@link VehiclePartService}.
 * <p>
 * This service orchestrates all business logic related to vehicle parts and their
 * corresponding inventory records. It handles creation, updates, logical deletion,
 * and stock management, ensuring data consistency between parts and their inventory.
 * </p>
 *
 * <h3>Exception Strategy</h3>
 * <p>
 * Methods in this class throw {@link EntityNotFoundException} if a requested resource
 * (like a VehiclePart, Vehicle, or Inventory record) is not found. It may also throw
 * {@link IllegalStateException} for business rule violations, such as attempting to
 * update stock for a deleted part.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class VehiclePartServiceImpl implements VehiclePartService {

    private final VehiclePartRepository vehiclePartRepository;
    private final VehiclePartInventoryRepository inventoryRepository;
    private final VehicleRepository vehicleRepository;
    private final HeadquarterService headquarterService;
    private final VehiclePartMapper vehiclePartMapper;

    private static final String VEHICLE_PART_NOT_FOUND = "VehiclePart not found with ID: ";

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public VehiclePartResponse createVehiclePartAndInventory(VehiclePartRequest request) {
        VehiclePart vehiclePart = vehiclePartMapper.toEntity(request);
        VehiclePart savedVehiclePart = vehiclePartRepository.save(vehiclePart);

        VehiclePartInventory inventory = new VehiclePartInventory();
        inventory.setVehiclePart(savedVehiclePart);
        inventory.setName(savedVehiclePart.getName());
        inventory.setQuantity(request.getQuantity());

        if (request.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId().longValue())
                    .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));

            inventory.setHeadquarter(vehicle.getHeadquarter());
            inventory.setVehicle(request.getVehicleId());
            inventory.setVehicleAssociated(true);
        } else {
            Headquarter mainHeadquarter = headquarterService.getMainHeadquarter();
            inventory.setHeadquarter(mainHeadquarter);
            inventory.setVehicleAssociated(false);
        }

        inventoryRepository.save(inventory);

        return vehiclePartMapper.toResponse(savedVehiclePart);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public VehiclePartResponse updateVehiclePart(Long id, VehiclePartRequest request) {
        VehiclePart existingPart = vehiclePartRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(VEHICLE_PART_NOT_FOUND + id));

        String oldName = existingPart.getName();
        String newName = request.getName();

        vehiclePartMapper.updateEntityFromRequest(request, existingPart);
        VehiclePart updatedPart = vehiclePartRepository.save(existingPart);

        if (newName != null && !newName.isBlank() && !newName.equals(oldName)) {
            List<VehiclePartInventory> inventories = inventoryRepository.findAllByVehiclePartId(id);
            inventories.forEach(inventory -> inventory.setName(newName));
            inventoryRepository.saveAll(inventories);
        }

        return vehiclePartMapper.toResponse(updatedPart);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateStock(Long partId, Long headquarterId, UpdateStockRequest request) {
        VehiclePartInventory inventory = inventoryRepository.findByVehiclePartIdAndHeadquarterId(partId, headquarterId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory record not found for part " + partId + " at headquarter " + headquarterId));

        if(inventory.getVehiclePart().isDeleted()){
            throw new IllegalStateException("Cannot update stock for a deleted part with ID: " + partId);
        }

        inventory.setQuantity(request.getQuantity());
        inventoryRepository.save(inventory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VehiclePartResponse getVehiclePartById(Long id) {
        VehiclePart part = vehiclePartRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(VEHICLE_PART_NOT_FOUND + id));
        return vehiclePartMapper.toResponse(part);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteVehiclePart(Long id) {
        VehiclePart partToDelete = vehiclePartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VEHICLE_PART_NOT_FOUND + id));

        partToDelete.setDeleted(true);
        vehiclePartRepository.save(partToDelete);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<VehiclePartResponse> getPage(String name, Long vehicleId, Long headquarterId, Pageable pageable) {
        Page<VehiclePart> page = vehiclePartRepository.findWithFilters(
                nullIfBlank(name),
                vehicleId,
                headquarterId,
                pageable
        );
        return page.map(vehiclePartMapper::toResponse);
    }

    private String nullIfBlank(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
