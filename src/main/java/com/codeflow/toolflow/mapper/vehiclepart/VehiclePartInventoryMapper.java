package com.codeflow.toolflow.mapper.vehiclepart;

import com.codeflow.toolflow.dto.vehiclepart.VehiclePartResponse;
import com.codeflow.toolflow.persistence.vehiclepart.entity.VehiclePartInventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting VehiclePartInventory entities into detailed DTOs for API responses.
 */
@Mapper(componentModel = "spring")
public interface VehiclePartInventoryMapper {

    /**
     * Maps a VehiclePartInventory entity to an InventoryDetail DTO.
     * It extracts the headquarter's ID and name for a clear and concise response.
     *
     * @param inventory The source VehiclePartInventory entity.
     * @return The resulting InventoryDetail DTO.
     */
    @Mapping(source = "headquarter.id", target = "headquarterId")
    @Mapping(source = "headquarter.name", target = "headquarterName")
    @Mapping(source = "vehicle", target = "vehicleId")
    @Mapping(source = "quantity", target = "quantity")
    VehiclePartResponse.InventoryDetail toInventoryDetail(VehiclePartInventory inventory);
}
