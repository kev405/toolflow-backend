package com.codeflow.toolflow.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-roles")
public class EndpointsTestRoles {

    @GetMapping("/test-administrator")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String testAdministrator() {
        return "test-administrator";
    }

    @GetMapping("/test-teacher")
    @PreAuthorize("hasAnyRole('TEACHER')")
    public String testTeacher() {
        return "test-teacher";
    }

    @GetMapping("/test-student")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public String testStudent() {
        return "test-student";
    }

    @GetMapping("/test-tool-administrator")
    @PreAuthorize("hasAnyRole('TOOL_ADMINISTRATOR')")
    public String testToolAdministrator() {
        return "test-tool-administrator";
    }
}
