package com.codeflow.toolflow.mapper.vehicle;

import com.codeflow.toolflow.dto.vehicle.VehicleRequest;
import com.codeflow.toolflow.dto.vehicle.VehicleResponse;
import com.codeflow.toolflow.persistence.headquarter.entity.Headquarter;
import com.codeflow.toolflow.persistence.vehicle.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    Vehicle toEntity(VehicleRequest dto);

    @Mapping(target = "headquarter", expression = "java(mapHeadquarter(entity.getHeadquarter()))")
    VehicleResponse toResponse(Vehicle entity);

    default VehicleResponse.HeadquarterSummary mapHeadquarter(Headquarter headquarter) {
        if (headquarter == null) return null;
        return VehicleResponse.HeadquarterSummary.builder()
                .id(headquarter.getId())
                .name(headquarter.getName())
                .build();
    }
}
