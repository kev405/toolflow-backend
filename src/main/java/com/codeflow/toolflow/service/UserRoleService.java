package com.codeflow.toolflow.service;

import java.util.List;
import com.codeflow.toolflow.dto.RUserRole;
import com.codeflow.toolflow.persistence.entity.UserRole;
import com.codeflow.toolflow.util.enums.Role;

public interface UserRoleService {

    public UserRole create(RUserRole userRole);

    public void delete(RUserRole userRole);

    public List<Role> getAllRoles();
}
