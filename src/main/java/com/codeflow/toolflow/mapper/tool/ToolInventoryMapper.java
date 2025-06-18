package com.codeflow.toolflow.mapper.tool;

import com.codeflow.toolflow.dto.tool.ToolInventoryResponse;
import com.codeflow.toolflow.persistence.tool.entity.ToolInventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ToolInventoryMapper {
    @Mapping(source = "headquarter.id", target = "headquarterId")
    @Mapping(source = "headquarter.name", target = "name")
    @Mapping(source = "headquarter.main", target = "main")
    ToolInventoryResponse toResponse(ToolInventory inventory);
}
