package com.codeflow.toolflow.mapper.vehicle;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.codeflow.toolflow.dto.vehicle.VehicleRequest;
import com.codeflow.toolflow.dto.vehicle.VehicleResponse;
import com.codeflow.toolflow.persistence.vehicle.entity.Vehicle;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    Vehicle toEntity(VehicleRequest dto);

    VehicleResponse toResponse(Vehicle entity);
}
