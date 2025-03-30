package com.codeflow.toolflow.controller.user;

import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.codeflow.toolflow.dto.RUserRole;
import com.codeflow.toolflow.persistence.user.entity.UserRole;
import com.codeflow.toolflow.service.user.UserRoleService;
import com.codeflow.toolflow.util.enums.Role;

/**
 * REST controller for handling user role operations.
 *
 * Provides endpoints to manage user roles in the system, including retrieving all roles,
 * creating new user roles, and deleting existing user roles. Access to these endpoints
 * is restricted to users with the 'ADMINISTRATOR' role.
 */
@RestController
@RequestMapping("/user-role")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @GetMapping("/all-role")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public List<Role> getAllRoles() {
        return userRoleService.getAllRoles();
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public UserRole create(@RequestBody RUserRole rUserRole) {
        return userRoleService.create(rUserRole);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void delete(@RequestBody RUserRole rUserRole) {
        userRoleService.delete(rUserRole);
    }
}
