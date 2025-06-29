package com.codeflow.toolflow.mapper.vehiclepart;

import com.codeflow.toolflow.dto.vehiclepart.VehiclePartRequest;
import com.codeflow.toolflow.dto.vehiclepart.VehiclePartResponse;
import com.codeflow.toolflow.persistence.vehiclepart.entity.VehiclePart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for converting between VehiclePart DTOs and entities.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VehiclePartMapper {

    /**
     * Converts a VehiclePartRequest DTO to a VehiclePart entity.
     *
     * @param dto The source DTO.
     * @return The resulting VehiclePart entity.
     */
    VehiclePart toEntity(VehiclePartRequest dto);

    /**
     * Converts a VehiclePart entity to a VehiclePartResponse DTO.
     *
     * @param entity The source entity.
     * @return The resulting VehiclePartResponse DTO.
     */
    VehiclePartResponse toResponse(VehiclePart entity);

    /**
     * Updates an existing VehiclePart entity from a VehiclePartRequest DTO.
     * Null fields in the DTO will be ignored. The entity's ID will not be modified.
     *
     * @param dto The source DTO with update data.
     * @param entity The target entity to be updated.
     */
    @Mapping(target = "id", ignore = true) // Ensure the ID is not overwritten
    void updateEntityFromRequest(VehiclePartRequest dto, @MappingTarget VehiclePart entity);
}
