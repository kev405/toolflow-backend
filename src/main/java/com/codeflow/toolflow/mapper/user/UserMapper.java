package com.codeflow.toolflow.mapper.user;

import com.codeflow.toolflow.dto.user.UserRequest;
import com.codeflow.toolflow.dto.user.UserResponse;
import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.persistence.user.entity.UserRole;
import com.codeflow.toolflow.util.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequest request);

    @Mapping(target = "roles", ignore = true)
    UserResponse toResponseWithoutRoles(User user);

    default UserResponse toResponse(User user) {
        UserResponse response = toResponseWithoutRoles(user);

        if (user.getUserRoles() != null) {
            List<Role> roleResponses = user.getUserRoles().stream()
                    .map(UserRole::getRole)
                    .toList();
            response.setRoles(roleResponses);
        }

        return response;
    }
}


