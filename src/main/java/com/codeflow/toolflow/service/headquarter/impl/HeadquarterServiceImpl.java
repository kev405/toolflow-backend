package com.codeflow.toolflow.service.headquarter.impl;

import com.codeflow.toolflow.dto.auth.UserLogin;
import com.codeflow.toolflow.dto.headquarter.HeadquarterRequest;
import com.codeflow.toolflow.dto.headquarter.HeadquarterResponse;
import com.codeflow.toolflow.mapper.headquarter.HeadquarterMapper;
import com.codeflow.toolflow.persistence.headquarter.entity.Headquarter;
import com.codeflow.toolflow.persistence.headquarter.repository.HeadquarterRepository;
import com.codeflow.toolflow.persistence.tool.repository.ToolInventoryRepository;
import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.persistence.user.repository.UserRepository;
import com.codeflow.toolflow.persistence.vehicle.repository.VehicleRepository;
import com.codeflow.toolflow.persistence.vehiclepart.repository.VehiclePartInventoryRepository;
import com.codeflow.toolflow.service.headquarter.HeadquarterService;
import com.codeflow.toolflow.util.exception.AssociatedEntitiesExistException;
import com.codeflow.toolflow.util.exception.MainHeadquarterDeletionException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HeadquarterServiceImpl implements HeadquarterService {

    private final HeadquarterRepository headquarterRepository;
    private final UserRepository userRepository;
    private final HeadquarterMapper headquarterMapper;
    private final VehicleRepository vehicleRepository;
    private final ToolInventoryRepository toolInventoryRepository;
    private final VehiclePartInventoryRepository vehiclePartInventoryRepository;

    @Override
    @Transactional
    public HeadquarterResponse registerOne(HeadquarterRequest request) {
        User responsible = userRepository.findById(request.getResponsibleId())
                .orElseThrow(() -> new EntityNotFoundException("Responsible user not found"));

        Headquarter headquarter = headquarterMapper.toEntity(request);
        headquarter.setResponsible(responsible);
        headquarter.setMain(false);
        headquarter.setStatus(true);
        headquarter.setCreatedAt(LocalDateTime.now());
        headquarter.setCreatedBy(getCurrentUserId());
        headquarter.setUpdatedAt(LocalDateTime.now());
        headquarter.setUpdatedBy(getCurrentUserId());

        return headquarterMapper.toResponse(headquarterRepository.save(headquarter));
    }

    @Override
    @Transactional
    public HeadquarterResponse updateOne(Long id, HeadquarterRequest request) {
        Headquarter headquarter = headquarterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Headquarter not found"));

        User responsible = userRepository.findById(request.getResponsibleId())
                .orElseThrow(() -> new EntityNotFoundException("Responsible user not found"));

        headquarter.setName(request.getName());
        headquarter.setAddress(request.getAddress());
        headquarter.setResponsible(responsible);
        headquarter.setUpdatedAt(LocalDateTime.now());
        headquarter.setUpdatedBy(getCurrentUserId());

        return headquarterMapper.toResponse(headquarterRepository.save(headquarter));
    }

    @Override
    @Transactional
    public void deleteOne(Long id) {
        Headquarter headquarter = headquarterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La sede no fue encontrada con el id: " + id));

        if (Boolean.TRUE.equals(headquarter.getMain())) {
            throw new MainHeadquarterDeletionException("No es posible eliminar la sede principal.");
        }

        boolean hasVehicles = vehicleRepository.existsByHeadquarterId(id);
        boolean hasToolsWithStock = toolInventoryRepository.existsByHeadquarterIdAndAvailableGreaterThan(id, 0);
        boolean hasVehiclePartsWithStock = vehiclePartInventoryRepository.existsByHeadquarterIdAndQuantityGreaterThan(id, 0);

        if (hasVehicles || hasToolsWithStock || hasVehiclePartsWithStock) {
            StringBuilder sb = new StringBuilder("No se puede eliminar la sede porque tiene entidades asociadas: ");
            if (hasVehicles) sb.append("[Vehículos] ");
            if (hasToolsWithStock) sb.append("[Herramientas con stock disponible] ");
            if (hasVehiclePartsWithStock) sb.append("[Partes de vehículo con stock disponible] ");
            throw new AssociatedEntitiesExistException(sb.toString().trim());
        }

        headquarter.setStatus(false);
        headquarter.setUpdatedAt(LocalDateTime.now());
        headquarter.setUpdatedBy(getCurrentUserId());
        headquarterRepository.save(headquarter);
    }

    @Override
    public HeadquarterResponse getOne(Long id) {
        return headquarterRepository.findById(id)
                .map(headquarterMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Headquarter not found"));
    }

    @Override
    public Headquarter getOneEntity(Long id) {
        return headquarterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Headquarter not found"));
    }

    @Override
    public List<HeadquarterResponse> getAll() {
        return headquarterRepository.findAll().stream()
                .map(headquarterMapper::toResponse)
                .toList();
    }

    @Override
    public Page<HeadquarterResponse> getPage(Pageable pageable) {
        return headquarterRepository.findAllByStatusTrue(pageable)
                .map(headquarterMapper::toResponse);
    }

    @Override
    public Headquarter getMainHeadquarter() {
        return headquarterRepository.findByMainTrue()
                .orElseThrow(() -> new EntityNotFoundException("Main headquarter not found"));
    }

    private Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserLogin userDetails) {
            return userDetails.getId();
        }
        throw new IllegalStateException("No authenticated user found.");
    }
}
