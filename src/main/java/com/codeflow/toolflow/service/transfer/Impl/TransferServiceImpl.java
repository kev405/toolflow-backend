package com.codeflow.toolflow.service.transfer.Impl;

import com.codeflow.toolflow.dto.auth.UserLogin;
import com.codeflow.toolflow.dto.transfer.TransferRequest;
import com.codeflow.toolflow.dto.transfer.TransferResponse;
import com.codeflow.toolflow.mapper.transfer.TransferMapper;
import com.codeflow.toolflow.persistence.headquarter.entity.Headquarter;
import com.codeflow.toolflow.persistence.headquarter.repository.HeadquarterRepository;
import com.codeflow.toolflow.persistence.tool.entity.Tool;
import com.codeflow.toolflow.persistence.tool.entity.ToolInventory;
import com.codeflow.toolflow.persistence.tool.repository.ToolInventoryRepository;
import com.codeflow.toolflow.persistence.tool.repository.ToolRepository;
import com.codeflow.toolflow.persistence.transfer.entity.Transfer;
import com.codeflow.toolflow.persistence.transfer.entity.TransferTool;
import com.codeflow.toolflow.persistence.transfer.entity.TransferVehicle;
import com.codeflow.toolflow.persistence.transfer.entity.TransferVehiclePart;
import com.codeflow.toolflow.persistence.transfer.repository.TransferRepository;
import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.persistence.user.repository.UserRepository;
import com.codeflow.toolflow.persistence.vehicle.entity.Vehicle;
import com.codeflow.toolflow.persistence.vehicle.repository.VehicleRepository;
import com.codeflow.toolflow.persistence.vehiclepart.entity.VehiclePart;
import com.codeflow.toolflow.persistence.vehiclepart.entity.VehiclePartInventory;
import com.codeflow.toolflow.persistence.vehiclepart.repository.VehiclePartInventoryRepository;
import com.codeflow.toolflow.persistence.vehiclepart.repository.VehiclePartRepository;
import com.codeflow.toolflow.service.transfer.TransferService;
import com.codeflow.toolflow.util.enums.TransferStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

/**
 * Default implementation of {@link TransferService}.
 * <p>
 * This service manages the entire lifecycle of an asset transfer, from creation
 * and validation to final processing (acceptance or cancellation). It ensures
 * transactional integrity for all inventory movements across multiple headquarters.
 * </p>
 *
 * <h3>Exception Strategy</h3>
 * <p>
 * This service throws specific exceptions for different failure scenarios:
 * <ul>
 * <li>{@link EntityNotFoundException}: When a required entity (User, Headquarter, Item) is not found.</li>
 * <li>{@link IllegalArgumentException}: For invalid request data, like duplicate items or same origin/destination.</li>
 * <li>{@link IllegalStateException}: For business rule violations, such as insufficient stock or invalid state transitions.</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;
    private final UserRepository userRepository;
    private final HeadquarterRepository headquarterRepository;
    private final VehicleRepository vehicleRepository;
    private final VehiclePartRepository vehiclePartRepository;
    private final VehiclePartInventoryRepository vehiclePartInventoryRepository;
    private final ToolRepository toolRepository;
    private final ToolInventoryRepository toolInventoryRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TransferResponse createTransfer(TransferRequest request) {
        validateHeadquarters(request.getOriginHeadquarterId(), request.getDestinationHeadquarterId());
        validateItemUniqueness(request);
        validateStockAndOwnership(request);

        Transfer transfer = transferMapper.toEntity(request);

        User responsible = userRepository.findById(request.getResponsibleId())
                .orElseThrow(() -> new EntityNotFoundException("Responsible user not found"));
        Headquarter origin = headquarterRepository.findById(request.getOriginHeadquarterId())
                .orElseThrow(() -> new EntityNotFoundException("Origin headquarter not found"));
        Headquarter destination = headquarterRepository.findById(request.getDestinationHeadquarterId())
                .orElseThrow(() -> new EntityNotFoundException("Destination headquarter not found"));

        transfer.setResponsible(responsible);
        transfer.setOriginHeadquarter(origin);
        transfer.setDestinationHeadquarter(destination);

        enrichTransferItems(transfer, request);

        Transfer savedTransfer = transferRepository.save(transfer);
        return transferMapper.toResponse(savedTransfer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TransferResponse updateTransfer(Long transferId, TransferRequest request) {
        // 1. Encontrar la transferencia existente
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new EntityNotFoundException("Transfer not found with ID: " + transferId));

        // 2. Validar que el estado sea 'PENDING' para permitir la actualización
        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new IllegalStateException("Only PENDING transfers can be updated. Current status: " + transfer.getStatus());
        }

        // 3. Re-validar los datos de la solicitud (reglas de negocio y stock)
        validateHeadquarters(request.getOriginHeadquarterId(), request.getDestinationHeadquarterId());
        validateItemUniqueness(request);
        validateStockAndOwnership(request);

        // 4. Actualizar los datos de la transferencia
        User responsible = userRepository.findById(request.getResponsibleId())
                .orElseThrow(() -> new EntityNotFoundException("Responsible user not found"));
        Headquarter origin = headquarterRepository.findById(request.getOriginHeadquarterId())
                .orElseThrow(() -> new EntityNotFoundException("Origin headquarter not found"));
        Headquarter destination = headquarterRepository.findById(request.getDestinationHeadquarterId())
                .orElseThrow(() -> new EntityNotFoundException("Destination headquarter not found"));

        transfer.setResponsible(responsible);
        transfer.setOriginHeadquarter(origin);
        transfer.setDestinationHeadquarter(destination);
        transfer.setNotes(request.getNotes());

        // 5. Limpiar las listas de items anteriores. Gracias a 'orphanRemoval=true' en la entidad Transfer,
        // esto eliminará los registros asociados de las tablas transfer_tools, etc.
        transfer.getTools().clear();
        transfer.getVehicleParts().clear();
        transfer.getVehicles().clear();

        // 6. Mapear los nuevos items desde la solicitud a la entidad existente
        Transfer updatedItems = transferMapper.toEntity(request);

        updatedItems.getTools().forEach(item -> {
            item.setTransfer(transfer);
            transfer.getTools().add(item);
        });

        updatedItems.getVehicleParts().forEach(item -> {
            item.setTransfer(transfer);
            transfer.getVehicleParts().add(item);
        });

        updatedItems.getVehicles().forEach(item -> {
            item.setTransfer(transfer);
            transfer.getVehicles().add(item);
        });

        // 7. Enriquecer los nuevos items con las entidades completas (Tool, VehiclePart)
        enrichTransferItems(transfer, request);

        // 8. Guardar la entidad actualizada y devolver la respuesta
        Transfer savedTransfer = transferRepository.save(transfer);
        return transferMapper.toResponse(savedTransfer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TransferResponse acceptTransfer(Long transferId) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new EntityNotFoundException("Transfer not found"));

        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new IllegalStateException("Only PENDING transfers can be accepted. Current status: " + transfer.getStatus());
        }

        TransferRequest request = transferMapper.toRequest(transfer);

        validateStockAndOwnership(request);

        processVehicleTransfers(transfer);
        processToolTransfers(transfer);
        processVehiclePartTransfers(transfer);

        transfer.setStatus(TransferStatus.ACCEPTED);
        Transfer acceptedTransfer = transferRepository.save(transfer);

        return transferMapper.toResponse(acceptedTransfer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TransferResponse> getAllTransfers(
            Long originId,
            Long destinationId,
            String transferDate,
            List<Long> toolIds,
            List<Long> partIds,
            List<Long> vehicleIds,
            Pageable pageable) {

        OffsetDateTime startDate = null;
        OffsetDateTime endDate = null;

        // Si se proporciona una fecha, creamos un rango para todo ese día.
        if (transferDate != null && !transferDate.isBlank()) {
            LocalDate date = LocalDate.parse(transferDate); // Asume formato "YYYY-MM-DD"
            startDate = date.atStartOfDay().atOffset(ZoneOffset.UTC);
            endDate = date.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);
        }

        // Las listas vacías deben ser tratadas como nulas para que la consulta JPQL funcione correctamente.
        List<Long> finalToolIds = (toolIds != null && !toolIds.isEmpty()) ? toolIds : null;
        List<Long> finalPartIds = (partIds != null && !partIds.isEmpty()) ? partIds : null;
        List<Long> finalVehicleIds = (vehicleIds != null && !vehicleIds.isEmpty()) ? vehicleIds : null;


        Page<Transfer> transfers = transferRepository.findWithFilters(
                originId,
                destinationId,
                startDate,
                endDate,
                finalToolIds,
                finalPartIds,
                finalVehicleIds,
                pageable
        );

        return transfers.map(transferMapper::toResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TransferResponse cancelTransfer(Long transferId) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new EntityNotFoundException("Transfer not found"));

        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new IllegalStateException("Only PENDING transfers can be cancelled. Current status: " + transfer.getStatus());
        }

        transfer.setStatus(TransferStatus.CANCELLED);
        Transfer cancelledTransfer = transferRepository.save(transfer);
        return transferMapper.toResponse(cancelledTransfer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public TransferResponse getTransferById(Long transferId) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new EntityNotFoundException("Transfer not found"));
        return transferMapper.toResponse(transfer);
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional(readOnly = true)
//    public Page<TransferResponse> getAllTransfers(Pageable pageable) {
//        return transferRepository.findAll(pageable).map(transferMapper::toResponse);
//    }

    private void validateHeadquarters(Long originId, Long destId) {
        if (originId.equals(destId)) {
            throw new IllegalArgumentException("Origin and destination headquarters must be different.");
        }
    }

    private void validateItemUniqueness(TransferRequest request) {
        if (request.getTools() != null) {
            long distinctCount = request.getTools().stream().map(TransferRequest.ToolItem::getToolId).distinct().count();
            if (distinctCount < request.getTools().size()) {
                throw new IllegalArgumentException("Duplicate tool IDs found in the transfer request.");
            }
        }
        if (request.getVehicleParts() != null) {
            long distinctCount = request.getVehicleParts().stream().map(TransferRequest.PartItem::getPartId).distinct().count();
            if (distinctCount < request.getVehicleParts().size()) {
                throw new IllegalArgumentException("Duplicate part IDs found in the transfer request.");
            }
        }
    }

    private void enrichTransferItems(Transfer transfer, TransferRequest request) {
        if (request.getTools() != null) {
            for (int i = 0; i < request.getTools().size(); i++) {
                Long toolId = request.getTools().get(i).getToolId();
                Tool tool = toolRepository.findById(toolId).orElseThrow(() -> new EntityNotFoundException("Tool not found: " + toolId));
                transfer.getTools().get(i).setTool(tool);
            }
        }
        if (request.getVehicleParts() != null) {
            for (int i = 0; i < request.getVehicleParts().size(); i++) {
                Long partId = request.getVehicleParts().get(i).getPartId();
                VehiclePart part = vehiclePartRepository.findById(partId).orElseThrow(() -> new EntityNotFoundException("Part not found: " + partId));
                transfer.getVehicleParts().get(i).setVehiclePart(part);
            }
        }
    }

    private void validateStockAndOwnership(TransferRequest request) {
        if (request.getTools() != null) {
            for (TransferRequest.ToolItem item : request.getTools()) {
                ToolInventory originInventory = toolInventoryRepository.findByToolIdAndHeadquarterId(item.getToolId(), request.getOriginHeadquarterId())
                        .orElseThrow(() -> new EntityNotFoundException("Tool " + item.getToolId() + " not found in origin headquarter."));
                if (originInventory.getAvailable() < item.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock for tool " + item.getToolId() + " at origin headquarter.");
                }
            }
        }

        if (request.getVehicleParts() != null) {
            for (TransferRequest.PartItem item : request.getVehicleParts()) {
                VehiclePartInventory originInventory = vehiclePartInventoryRepository.
                        findAvailableNonAssociatedPartsByVehiclePartIdAndHeadquarter(item.getPartId(), request.getOriginHeadquarterId())
                        .orElseThrow(() -> new EntityNotFoundException("Part " + item.getPartId() + " not found in origin headquarter."));
                if (originInventory.getVehicleAssociated()) {
                    throw new IllegalStateException("Part " + item.getPartId() + " is associated with a vehicle and cannot be transferred individually.");
                }
                if (originInventory.getQuantity() < item.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock for part " + item.getPartId() + " at origin headquarter.");
                }
            }
        }

        if (request.getVehicles() != null) {
            for (Long vehicleId : request.getVehicles()) {
                Vehicle vehicle = vehicleRepository.findById(vehicleId)
                        .orElseThrow(() -> new EntityNotFoundException("Vehicle not found: " + vehicleId));
                if (!vehicle.getHeadquarter().getId().equals(request.getOriginHeadquarterId())) {
                    throw new IllegalStateException("Vehicle " + vehicleId + " does not belong to the origin headquarter.");
                }
            }
        }
    }

    private void processVehicleTransfers(Transfer transfer) {
        Headquarter destination = transfer.getDestinationHeadquarter();
        for (TransferVehicle tv : transfer.getVehicles()) {
            Vehicle vehicle = tv.getVehicle();
            vehicle.setHeadquarter(destination);
            vehicleRepository.save(vehicle);

            List<VehiclePartInventory> associatedPartsInventory = vehiclePartInventoryRepository.findAllByVehicle(vehicle.getId());
            for (VehiclePartInventory partInventory : associatedPartsInventory) {
                partInventory.setHeadquarter(destination);
                vehiclePartInventoryRepository.save(partInventory);
            }
        }
    }

    private void processToolTransfers(Transfer transfer) {
        Headquarter origin = transfer.getOriginHeadquarter();
        Headquarter destination = transfer.getDestinationHeadquarter();

        for (TransferTool tt : transfer.getTools()) {
            int quantity = tt.getQuantity();
            Tool tool = tt.getTool();

            ToolInventory originInventory = toolInventoryRepository.findByToolIdAndHeadquarterId(tool.getId(), origin.getId()).orElseThrow();
            originInventory.setQuantity(originInventory.getQuantity() - quantity);
            originInventory.setAvailable(originInventory.getAvailable() - quantity);
            toolInventoryRepository.save(originInventory);

            ToolInventory destInventory = toolInventoryRepository.findByToolIdAndHeadquarterId(tool.getId(), destination.getId())
                    .orElseGet(() -> createNewToolInventory(tool, destination));

            destInventory.setQuantity(destInventory.getQuantity() + quantity);
            destInventory.setAvailable(destInventory.getAvailable() + quantity);
            toolInventoryRepository.save(destInventory);
        }
    }

    private void processVehiclePartTransfers(Transfer transfer) {
        Headquarter origin = transfer.getOriginHeadquarter();
        Headquarter destination = transfer.getDestinationHeadquarter();

        for (TransferVehiclePart tvp : transfer.getVehicleParts()) {
            int quantity = tvp.getQuantity();
            VehiclePart part = tvp.getVehiclePart();

            VehiclePartInventory originInventory = vehiclePartInventoryRepository.
                    findAvailableNonAssociatedPartsByVehiclePartIdAndHeadquarter(part.getId(), origin.getId()).orElseThrow();
            originInventory.setQuantity(originInventory.getQuantity() - quantity);
            vehiclePartInventoryRepository.save(originInventory);

            VehiclePartInventory destInventory = vehiclePartInventoryRepository.
                    findAvailableNonAssociatedPartsByVehiclePartIdAndHeadquarter(part.getId(), destination.getId())
                    .orElseGet(() -> createNewPartInventory(part, destination));

            destInventory.setQuantity(destInventory.getQuantity() + quantity);
            vehiclePartInventoryRepository.save(destInventory);
        }
    }

    private ToolInventory createNewToolInventory(Tool tool, Headquarter headquarter) {
        return ToolInventory.builder()
                .tool(tool)
                .headquarter(headquarter)
                .quantity(0)
                .available(0)
                .onLoan(0)
                .damaged(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(getCurrentUserId())
                .updatedBy(getCurrentUserId())
                .build();
    }

    private VehiclePartInventory createNewPartInventory(VehiclePart part, Headquarter headquarter) {
        return VehiclePartInventory.builder()
                .vehiclePart(part)
                .headquarter(headquarter)
                .name(part.getName())
                .quantity(0)
                .vehicleAssociated(false)
                .build();
    }

    private Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserLogin userDetails) {
            return userDetails.getId();
        }
        throw new IllegalStateException("No authenticated user found.");
    }
}
