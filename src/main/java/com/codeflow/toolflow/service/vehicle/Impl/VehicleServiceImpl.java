package com.codeflow.toolflow.service.vehicle.Impl;

import com.codeflow.toolflow.dto.vehicle.VehicleRequest;
import com.codeflow.toolflow.dto.vehicle.VehicleResponse;
import com.codeflow.toolflow.mapper.vehicle.VehicleMapper;
import com.codeflow.toolflow.persistence.vehicle.entity.Vehicle;
import com.codeflow.toolflow.persistence.vehicle.repository.VehicleRepository;
import com.codeflow.toolflow.service.vehicle.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public VehicleResponse registerOneVehicle(VehicleRequest VehicleRequest) {
        Vehicle entity = vehicleMapper.toEntity(VehicleRequest);
        Vehicle entitySaved = vehicleRepository.save(entity);
        return vehicleMapper.toResponse(entitySaved);
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
     * {@link VehicleRepository#queryPageable(String, String, String, String, String, String, String, Pageable)}.
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
                                         Pageable pageable) {

        return vehicleRepository
                .queryPageable(vehicleType, plate, model, color, numberChasis,
                        brand, location, pageable)
                .map(vehicleMapper::toResponse);
    }
}
