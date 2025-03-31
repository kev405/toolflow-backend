package com.codeflow.toolflow.service.user;

import com.codeflow.toolflow.dto.RUserRole;
import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.persistence.user.entity.UserRole;
import com.codeflow.toolflow.util.enums.Role;

import java.util.List;

public interface UserRoleService {

    UserRole create(RUserRole userRole);

    void delete(RUserRole userRole);

    List<Role> getAllRoles();
}
