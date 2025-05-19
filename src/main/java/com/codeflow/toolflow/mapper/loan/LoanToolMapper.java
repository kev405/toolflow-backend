package com.codeflow.toolflow.mapper.loan;

import com.codeflow.toolflow.dto.loan.LoanResponse;
import com.codeflow.toolflow.dto.loan.LoanToolResponse;
import com.codeflow.toolflow.persistence.loan.entity.LoanTool;
import com.codeflow.toolflow.persistence.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanToolMapper {
    @Mapping(target = "responsible", expression = "java(mapResponsible(loanTool.getResponsible()))")
    @Mapping(source = "tool.id", target = "id")
    @Mapping(source = "tool.toolName", target = "toolName")
    LoanToolResponse toResponse(LoanTool loanTool);

    default LoanResponse.UserSummary mapResponsible(User responsible) {
        if (responsible == null) return null;
        return LoanResponse.UserSummary.builder()
                .id(responsible.getId())
                .fullName(responsible.getName() + " " + responsible.getLastName())
                .build();
    }
}