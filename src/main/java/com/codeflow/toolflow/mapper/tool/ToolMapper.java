package com.codeflow.toolflow.mapper.tool;

import com.codeflow.toolflow.dto.tool.ToolRequest;
import com.codeflow.toolflow.dto.tool.ToolResponse;
import com.codeflow.toolflow.dto.tool.ToolSimpleResponse;
import com.codeflow.toolflow.persistence.tool.entity.Tool;
import com.codeflow.toolflow.persistence.tool.entity.ToolInventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ToolInventoryMapper.class})
public interface ToolMapper {
    ToolResponse toResponse(Tool entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "category", ignore = true)
    Tool toNewEntity(ToolRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "available", ignore = true)
    @Mapping(target = "onLoan", ignore = true)
    @Mapping(target = "damaged", ignore = true)
    Tool toExistingEntity(ToolRequest dto);

    /**
     * Maps Tool and ToolInventory into ToolSimpleResponse.
     */
    @Mapping(target = "id", source = "tool.id")
    @Mapping(target = "toolName", source = "tool.toolName")
    @Mapping(target = "available", source = "inventory.available")
    ToolSimpleResponse toSimpleResponse(Tool tool, ToolInventory inventory);
}
