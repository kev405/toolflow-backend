package com.codeflow.toolflow.mapper.transfer;

import com.codeflow.toolflow.dto.transfer.TransferRequest;
import com.codeflow.toolflow.dto.transfer.TransferResponse;
import com.codeflow.toolflow.persistence.headquarter.entity.Headquarter;
import com.codeflow.toolflow.persistence.transfer.entity.Transfer;
import com.codeflow.toolflow.persistence.transfer.entity.TransferTool;
import com.codeflow.toolflow.persistence.transfer.entity.TransferVehicle;
import com.codeflow.toolflow.persistence.transfer.entity.TransferVehiclePart;
import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.persistence.vehicle.entity.Vehicle;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Main mapper for the Transfer aggregate. It orchestrates the mapping between
 * TransferRequest/TransferResponse DTOs and the Transfer entity graph.
 * It uses specialized mappers for each type of transferred item.
 */
@Mapper(componentModel = "spring", uses = {
        TransferToolMapper.class,
        TransferVehiclePartMapper.class,
        TransferVehicleMapper.class
})
public interface TransferMapper {

    // --- To Entity Mappings ---

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "responsible", ignore = true)
    @Mapping(target = "originHeadquarter", ignore = true)
    @Mapping(target = "destinationHeadquarter", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tools", source = "tools")
    @Mapping(target = "vehicleParts", source = "vehicleParts")
    @Mapping(target = "vehicles", expression = "java(mapVehicleIdsToEntities(dto.getVehicles()))")
    Transfer toEntity(TransferRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transfer", ignore = true)
    @Mapping(target = "tool", ignore = true)
    TransferTool toolItemToEntity(TransferRequest.ToolItem item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transfer", ignore = true)
    @Mapping(target = "vehiclePart", ignore = true)
    TransferVehiclePart partItemToEntity(TransferRequest.PartItem item);

    @AfterMapping
    default void setTransferInChildEntities(@MappingTarget Transfer transfer) {
        if (transfer.getTools() != null) {
            transfer.getTools().forEach(tool -> tool.setTransfer(transfer));
        }
        if (transfer.getVehicleParts() != null) {
            transfer.getVehicleParts().forEach(part -> part.setTransfer(transfer));
        }
        if (transfer.getVehicles() != null) {
            transfer.getVehicles().forEach(vehicle -> vehicle.setTransfer(transfer));
        }
    }

    default List<TransferVehicle> mapVehicleIdsToEntities(List<Long> vehicleIds) {
        if (vehicleIds == null) return null;
        return vehicleIds.stream().map(id -> {
            TransferVehicle tv = new TransferVehicle();
            Vehicle v = new Vehicle();
            v.setId(id);
            tv.setVehicle(v);
            return tv;
        }).collect(Collectors.toList());
    }

    // --- To Response Mappings ---

    @Mapping(source = "responsible", target = "responsible")
    @Mapping(source = "originHeadquarter", target = "originHeadquarter")
    @Mapping(source = "destinationHeadquarter", target = "destinationHeadquarter")
    @Mapping(source = "tools", target = "tools")
    @Mapping(source = "vehicleParts", target = "vehicleParts")
    @Mapping(source = "vehicles", target = "vehicles")
    TransferResponse toResponse(Transfer entity);

    default TransferResponse.UserSummary userToSummary(User user) {
        if (user == null) return null;
        return TransferResponse.UserSummary.builder().id(user.getId()).username(user.getUsername()).build();
    }

    default TransferResponse.HeadquarterSummary headquarterToSummary(Headquarter hq) {
        if (hq == null) return null;
        return TransferResponse.HeadquarterSummary.builder().id(hq.getId()).name(hq.getName()).build();
    }
}
