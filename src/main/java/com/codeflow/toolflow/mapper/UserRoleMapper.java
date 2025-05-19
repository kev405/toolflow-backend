package com.codeflow.toolflow.mapper;

import com.codeflow.toolflow.dto.RUserRole;
import com.codeflow.toolflow.persistence.user.entity.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {

    @Mapping(target = "toolflowUser", ignore = true)
    @Mapping(target = "id", ignore = true)
    UserRole fromDto(RUserRole rUserRole);

    RUserRole toDto(UserRole userRole);
}
