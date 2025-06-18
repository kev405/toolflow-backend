package com.codeflow.toolflow.mapper.headquarter;

import com.codeflow.toolflow.dto.headquarter.HeadquarterRequest;
import com.codeflow.toolflow.dto.headquarter.HeadquarterResponse;
import com.codeflow.toolflow.dto.headquarter.HeadquarterResponse.UserSummary;
import com.codeflow.toolflow.persistence.headquarter.entity.Headquarter;
import com.codeflow.toolflow.persistence.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HeadquarterMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "main", ignore = true)
    @Mapping(target = "responsible", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Headquarter toEntity(HeadquarterRequest request);

    @Mapping(target = "responsible", expression = "java(mapUserSummary(entity.getResponsible()))")
    HeadquarterResponse toResponse(Headquarter entity);

    default UserSummary mapUserSummary(User user) {
        if (user == null) return null;
        return UserSummary.builder()
                .id(user.getId())
                .fullName(user.getName() + " " + user.getLastName())
                .build();
    }
}
