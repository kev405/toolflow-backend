package com.codeflow.toolflow.mapper.transfer;

import com.codeflow.toolflow.dto.transfer.TransferResponse;
import com.codeflow.toolflow.persistence.transfer.entity.TransferVehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting TransferVehicle entities to their corresponding DTOs for responses.
 */
@Mapper(componentModel = "spring")
public interface TransferVehicleMapper {

    @Mapping(source = "vehicle.id", target = "vehicleId")
    @Mapping(source = "vehicle.plate", target = "plate")
    @Mapping(source = "vehicle.model", target = "model")
    TransferResponse.VehicleSummary toResponse(TransferVehicle transferVehicle);
}
