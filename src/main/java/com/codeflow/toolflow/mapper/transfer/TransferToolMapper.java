package com.codeflow.toolflow.mapper.transfer;

import com.codeflow.toolflow.dto.transfer.TransferResponse;
import com.codeflow.toolflow.persistence.transfer.entity.TransferTool;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting TransferTool entities to their corresponding DTOs for responses.
 */
@Mapper(componentModel = "spring")
public interface TransferToolMapper {

    @Mapping(source = "tool.id", target = "toolId")
    @Mapping(source = "tool.toolName", target = "toolName")
    TransferResponse.ToolItemResponse toResponse(TransferTool transferTool);
}
