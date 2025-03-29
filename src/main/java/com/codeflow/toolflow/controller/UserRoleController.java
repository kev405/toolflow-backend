package com.codeflow.toolflow.controller;

import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.codeflow.toolflow.dto.RUserRole;
import com.codeflow.toolflow.persistence.entity.UserRole;
import com.codeflow.toolflow.service.UserRoleService;
import com.codeflow.toolflow.util.enums.Role;

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
