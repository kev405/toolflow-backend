package com.codeflow.toolflow.service.vehiclepart.Impl;

import com.codeflow.toolflow.dto.vehiclepart.*;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private static final String VEHICLE_PART_NOT_FOUND = "VehiclePart not found with ID: ";
    private static final String INVENTORY_NOT_FOUND = "Inventory record not found for part ";
    private final VehiclePartRepository vehiclePartRepository;
    private final VehiclePartInventoryRepository inventoryRepository;
    private final VehicleRepository vehicleRepository;
    private final HeadquarterService headquarterService;
    private final VehiclePartMapper vehiclePartMapper;

    /**
     * {@inheritDoc}
     * This implementation has been refactored for clarity and correctness.
     */
    @Override
    @Transactional
    public VehiclePartResponse createVehiclePartAndInventory(VehiclePartRequest request) {
        // 1. Find or create the parent VehiclePart entity.
        // Using Optional allows us to cleanly handle both cases where the part exists or not.
        VehiclePart vehiclePart = vehiclePartRepository.findByNameAndIsDeletedFalse(request.getName())
                .orElseGet(() -> {
                    VehiclePart newPart = vehiclePartMapper.toEntity(request);
                    return vehiclePartRepository.save(newPart);
                });

        // 2. Now that we have a guaranteed VehiclePart, create the specific inventory for it.
        createInventoryForVehiclePart(request, vehiclePart);

        // 3. The vehiclePart object now contains the new inventory, so the response will be correct.
        return vehiclePartMapper.toResponse(vehiclePart);
    }

    /**
     * Creates a new inventory record for an existing VehiclePart.
     * It also synchronizes the bidirectional relationship by adding the new inventory
     * to the part's collection.
     *
     * @param request     The DTO containing inventory details.
     * @param vehiclePart The parent entity to which the inventory will be added.
     */
    public void createInventoryForVehiclePart(VehiclePartRequest request, VehiclePart vehiclePart) {
        // Check for duplicate inventory before creating.
        if (request.getVehicleId() != null) {
            // If associating with a vehicle, check if an inventory for this part and vehicle already exists.
            if (inventoryRepository.existsByVehiclePartIdAndVehicle(vehiclePart.getId(), request.getVehicleId())) {
                throw new DataIntegrityViolationException(
                        "An inventory record for part ID '" + vehiclePart.getId() + "' and vehicle ID '"
                                + request.getVehicleId() + "' already exists."
                );
            }
        } else {
            // If creating a generic inventory, check if one already exists for this part in the main headquarter.
            // This logic might need adjustment based on business rules (e.g., can a part have generic stock in multiple HQs?).
            Headquarter mainHeadquarter = headquarterService.getMainHeadquarter();
            if (inventoryRepository.findByVehiclePartIdAndHeadquarterId(vehiclePart.getId(), mainHeadquarter.getId()).isPresent()) {
                throw new DataIntegrityViolationException(
                        "A generic inventory record for part '" + vehiclePart.getName() + "' already exists in the main headquarter."
                );
            }
        }

        VehiclePartInventory inventory = new VehiclePartInventory();
        inventory.setVehiclePart(vehiclePart);
        inventory.setName(vehiclePart.getName());
        inventory.setQuantity(request.getQuantity());

        if (request.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));

            inventory.setHeadquarter(vehicle.getHeadquarter());
            inventory.setVehicle(request.getVehicleId());
            inventory.setVehicleAssociated(true);
        } else {
            Headquarter mainHeadquarter = headquarterService.getMainHeadquarter();
            inventory.setHeadquarter(mainHeadquarter);
            inventory.setVehicleAssociated(false);
        }

        vehiclePart.getInventories().add(inventory);

        inventoryRepository.save(inventory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public VehiclePartResponse updateVehiclePart(Long id, VehiclePartUpdateRequest request) {
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
     * Replaces the old association logic with a more robust inventory movement system.
     * This method moves a specified quantity of a part from a source association (generic or vehicle)
     * to a destination association within the same headquarter.
     *
     * @param partId        The ID of the vehicle part being moved.
     * @param headquarterId The ID of the headquarter where the transaction occurs.
     * @param request       The DTO containing source, destination, and quantity details.
     */
    @Override
    @Transactional
    public void associateVehicle(Long partId, Long headquarterId, MoveInventoryRequest request) {
        // 1. Validaciones Iniciales
        if (Objects.equals(request.getSourceVehicleId(), request.getDestinationVehicleId())) {
            throw new IllegalArgumentException("Source and destination associations cannot be the same.");
        }

        VehiclePart vehiclePart = vehiclePartRepository.findByIdAndIsDeletedFalse(partId)
                .orElseThrow(() -> new EntityNotFoundException(VEHICLE_PART_NOT_FOUND + partId));

        // 2. Localizar y Validar Inventario de Origen
        Optional<VehiclePartInventory>
                sourceInventoryOpt = (request.getSourceVehicleId() == null)
                ? inventoryRepository.findByVehiclePartIdAndHeadquarterIdAndVehicleIsNull(partId, headquarterId)
                : inventoryRepository.findByVehiclePartIdAndHeadquarterIdAndVehicle(partId, headquarterId, request.getSourceVehicleId());

        VehiclePartInventory sourceInventory = sourceInventoryOpt.orElseThrow(() ->
                new EntityNotFoundException("Source inventory not found for part " + partId + " with source association " + request.getSourceVehicleId()));

        if (sourceInventory.getQuantity() < request.getQuantity()) {
            throw new IllegalStateException("Insufficient stock in source inventory. Available: "
                    + sourceInventory.getQuantity() + ", Requested: " + request.getQuantity());
        }

        // 3. Localizar o Crear Inventario de Destino
        Optional<VehiclePartInventory> destInventoryOpt = (request.getDestinationVehicleId() == null)
                ? inventoryRepository.findByVehiclePartIdAndHeadquarterIdAndVehicleIsNull(partId, headquarterId)
                : inventoryRepository.findByVehiclePartIdAndHeadquarterIdAndVehicle(partId, headquarterId, request.getDestinationVehicleId());

        VehiclePartInventory destInventory = destInventoryOpt.orElseGet(() -> {
            VehiclePartInventory newInventory = new VehiclePartInventory();
            newInventory.setVehiclePart(vehiclePart);
            newInventory.setName(vehiclePart.getName());
            newInventory.setQuantity(0);
            newInventory.setHeadquarter(sourceInventory.getHeadquarter());

            if (request.getDestinationVehicleId() != null) {
                Vehicle destVehicle = vehicleRepository.findById(request.getDestinationVehicleId())
                        .orElseThrow(() -> new EntityNotFoundException("Destination vehicle not found with ID: " + request.getDestinationVehicleId()));
                if (!destVehicle.getHeadquarter().getId().equals(headquarterId)) {
                    throw new IllegalStateException("Destination vehicle is not in the same headquarter as the inventory.");
                }
                newInventory.setVehicle(request.getDestinationVehicleId());
                newInventory.setVehicleAssociated(true);
            } else {
                newInventory.setVehicle(null);
                newInventory.setVehicleAssociated(false);
            }
            return newInventory;
        });

        // 4. Realizar la Transacción de Stock
        sourceInventory.setQuantity(sourceInventory.getQuantity() - request.getQuantity());
        destInventory.setQuantity(destInventory.getQuantity() + request.getQuantity());

        // 5. Guardar Cambios y Limpiar
        if (sourceInventory.getQuantity() == 0) {
            inventoryRepository.delete(sourceInventory);
        } else {
            inventoryRepository.save(sourceInventory);
        }
        inventoryRepository.save(destInventory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateStock(Long partId, Long headquarterId, UpdateStockRequest request) {
        VehiclePartInventory inventory = inventoryRepository.findByVehiclePartIdAndHeadquarterId(partId, headquarterId)
                .orElseThrow(() -> new EntityNotFoundException(INVENTORY_NOT_FOUND + partId + " at headquarter " + headquarterId));

        if (inventory.getVehiclePart().isDeleted()) {
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
    public Page<VehiclePartResponse> getPage(String name, String brand, String model, Long vehicleId, Long headquarterId, Pageable pageable) {
        Page<VehiclePart> page = vehiclePartRepository.findWithFilters(
                nullIfBlank(name),
                nullIfBlank(model),
                nullIfBlank(brand),
                vehicleId,
                headquarterId,
                pageable
        );
        return page.map(vehiclePartMapper::toResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TransferablePartVehicleResponse> getAvailableVehicleParts(Long headquarterId) {
        return inventoryRepository.findAvailableNonAssociatedPartsByHeadquarter(headquarterId).stream()
                .map(inventory -> TransferablePartVehicleResponse.builder()
                        .id(inventory.getVehiclePart().getId())
                        .name(inventory.getVehiclePart().getName())
                        .availableQuantity(inventory.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TransferablePartVehicleResponse> getAllVehicleParts() {
        return vehiclePartRepository.findallNonDeletedParts().stream()
                .map(vehiclePart -> TransferablePartVehicleResponse.builder()
                        .id(vehiclePart.getId())
                        .name(vehiclePart.getName())
                        .availableQuantity(0)
                        .build())
                .collect(Collectors.toList());
    }

    private String nullIfBlank(String value) {
        return (value == null || value.isBlank()) ? null : "%" + value.toLowerCase() + "%";
    }
}
