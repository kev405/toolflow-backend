package com.codeflow.toolflow.mapper.loan;

import com.codeflow.toolflow.dto.loan.LoanRequest;
import com.codeflow.toolflow.dto.loan.LoanResponse;
import com.codeflow.toolflow.persistence.loan.entity.Loan;
import com.codeflow.toolflow.persistence.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = LoanToolMapper.class)
public interface LoanMapper {
    @Mapping(target = "loanTools", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "responsible", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "status", ignore = true)
    Loan toEntity(LoanRequest request);

    @Mapping(target = "teacher", expression = "java(mapTeacher(loan.getTeacher()))")
    @Mapping(target = "responsible", expression = "java(mapTeacher(loan.getResponsible()))")
    @Mapping(target = "tools", source = "loanTools")
    LoanResponse toResponse(Loan loan);

    default LoanResponse.UserSummary mapTeacher(User teacher) {
        if (teacher == null) return null;
        return LoanResponse.UserSummary.builder()
                .id(teacher.getId())
                .fullName(teacher.getName() + " " + teacher.getLastName())
                .build();
    }
}
