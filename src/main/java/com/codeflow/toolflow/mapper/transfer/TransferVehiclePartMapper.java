package com.codeflow.toolflow.mapper.transfer;

import com.codeflow.toolflow.dto.transfer.TransferResponse;
import com.codeflow.toolflow.persistence.transfer.entity.TransferVehiclePart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting TransferVehiclePart entities to their corresponding DTOs for responses.
 */
@Mapper(componentModel = "spring")
public interface TransferVehiclePartMapper {

    @Mapping(source = "vehiclePart.id", target = "partId")
    @Mapping(source = "vehiclePart.name", target = "partName")
    TransferResponse.PartItemResponse toResponse(TransferVehiclePart transferPart);
}
