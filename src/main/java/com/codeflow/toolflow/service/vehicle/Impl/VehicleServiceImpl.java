package com.codeflow.toolflow.service.vehicle.Impl;

import com.codeflow.toolflow.dto.vehicle.TransferableVehicleResponse;
import com.codeflow.toolflow.dto.vehicle.VehicleRequest;
import com.codeflow.toolflow.dto.vehicle.VehicleResponse;
import com.codeflow.toolflow.mapper.vehicle.VehicleMapper;
import com.codeflow.toolflow.persistence.vehicle.entity.Vehicle;
import com.codeflow.toolflow.persistence.vehicle.repository.VehicleRepository;
import com.codeflow.toolflow.service.headquarter.HeadquarterService;
import com.codeflow.toolflow.service.vehicle.VehicleService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link VehicleService}.
 * <p>
 * This service delegates persistence operations to {@link VehicleRepository}
 * and uses {@link VehicleMapper} to translate between DTOs and the
 * {@link Vehicle} entity.
 * </p>
 *
 * <h3>Exception strategy</h3>
 * <p>
 * If a requested vehicle is not found, the methods throw a generic
 * {@link RuntimeException}. In production you would typically replace this with
 * a custom exception (e.g.&nbsp;{@code VehicleNotFoundException}) and map it to
 * an appropriate HTTP status.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleMapper vehicleMapper;
    private final VehicleRepository vehicleRepository;
    private final HeadquarterService headquarterService;

    /**
     * {@inheritDoc}
     */
    @Override
    public VehicleResponse registerOneVehicle(VehicleRequest vehicleRequest) {
        Vehicle entity = vehicleMapper.toEntity(vehicleRequest);
        entity.setHeadquarter(headquarterService.getMainHeadquarter());
        Vehicle saved = vehicleRepository.save(entity);
        return vehicleMapper.toResponse(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TransferableVehicleResponse> getAvailableVehicles(Long headquarterId) {
        return vehicleRepository.findAllByHeadquarterId(headquarterId).stream()
                .map(vehicle -> TransferableVehicleResponse.builder()
                        .id(vehicle.getId())
                        .name(vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getPlate() + ")")
                        .availableQuantity(1) // A vehicle is a single unit
                        .build())
                .collect(Collectors.toList());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TransferableVehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(vehicle -> TransferableVehicleResponse.builder()
                        .id(vehicle.getId())
                        .name(vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getPlate() + ")")
                        .vehicleType(vehicle.getVehicleType())
                        .availableQuantity(1) // A vehicle is a single unit
                        .build())
                .collect(Collectors.toList());

    }

    /**
     * {@inheritDoc}
     * <p>
     * The method performs an <em>upsert</em>: if the ID contained in
     * {@code VehicleRequest} already exists, it updates the record; otherwise it
     * creates a new one.
     * </p>
     */
    @Override
    public VehicleResponse updateOneVehicle(VehicleRequest VehicleRequest) {
        Vehicle entity = vehicleMapper.toEntity(VehicleRequest);
        VehicleResponse entityToUpdate = getOne(VehicleRequest.getId());
        entity.setHeadquarter(headquarterService.getOneEntity(entityToUpdate.getHeadquarter().getId()));
        Vehicle entitySaved = vehicleRepository.save(entity);
        return vehicleMapper.toResponse(entitySaved);
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException if the vehicle is not found
     */
    @Override
    public VehicleResponse getOne(Long id) {
        Vehicle entity = vehicleRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        return vehicleMapper.toResponse(entity);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Currently this method performs a <strong>hard</strong> delete using
     * {@link VehicleRepository#delete(Object)}. If you need a true “soft
     * delete” (mark-as-inactive), adjust the implementation accordingly.
     * </p>
     *
     * @throws RuntimeException if the vehicle is not found
     */
    @Override
    public void deleteOneVehicle(Long id) {
        Vehicle entity = vehicleRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        vehicleRepository.delete(entity);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The actual filtering logic is delegated to
     * {@link VehicleRepository#queryPageable(String, String, String, String, String, String, String, Long, Pageable)}.
     * </p>
     */
    @Override
    public Page<VehicleResponse> getPage(String vehicleType,
                                         String plate,
                                         String model,
                                         String color,
                                         String numberChasis,
                                         String brand,
                                         String location,
                                         Long headquarterId, // 🆕
                                         Pageable pageable) {
        return vehicleRepository.queryPageable(
                nullIfBlank(vehicleType),
                nullIfBlank(plate),
                nullIfBlank(model),
                nullIfBlank(color),
                nullIfBlank(numberChasis),
                nullIfBlank(brand),
                nullIfBlank(location),
                headquarterId, // 🆕
                pageable
        ).map(vehicleMapper::toResponse);
    }

    private String nullIfBlank(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
