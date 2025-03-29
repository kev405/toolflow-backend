package com.codeflow.toolflow.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.codeflow.toolflow.dto.RUserRole;
import com.codeflow.toolflow.persistence.entity.UserRole;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {

    @Mapping(target = "toolflowUser", ignore = true)
    @Mapping(target = "id", ignore = true)
    UserRole fromDto(RUserRole rUserRole);

    RUserRole toDto(UserRole userRole);
}
