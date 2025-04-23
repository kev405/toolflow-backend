package com.codeflow.toolflow.mapper.tool;

import com.codeflow.toolflow.dto.tool.ToolRequest;
import com.codeflow.toolflow.dto.tool.ToolResponse;
import com.codeflow.toolflow.persistence.tool.entity.Tool;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ToolMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "category", ignore = true)
    Tool toEntity(ToolRequest dto);

    ToolResponse toResponse(Tool entity);
}
