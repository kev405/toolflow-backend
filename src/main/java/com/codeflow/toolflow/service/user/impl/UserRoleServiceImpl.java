package com.codeflow.toolflow.service.user.impl;

import com.codeflow.toolflow.dto.RUserRole;
import com.codeflow.toolflow.mapper.UserRoleMapper;
import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.persistence.user.entity.UserRole;
import com.codeflow.toolflow.persistence.user.repository.UserRepository;
import com.codeflow.toolflow.persistence.user.repository.UserRoleRepository;
import com.codeflow.toolflow.service.user.UserRoleService;
import com.codeflow.toolflow.util.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;

    private final UserRepository userRepository;
    private final UserRoleMapper userRoleMapper;

    @Override
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public UserRole create(RUserRole userRole) {
        UserRole entity = userRoleMapper.fromDto(userRole);
        entity.setToolflowUser(userRepository.findById(userRole.getToolflowUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        UserRole entitySaved = userRoleRepository.save(entity);
        entitySaved.getToolflowUser().setPassword(null);
        return entitySaved;
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public void delete(RUserRole userRole) {
        UserRole entity = userRoleMapper.fromDto(userRole);
        entity.setId(userRole.getId());
        entity.setToolflowUser(userRepository.findById(userRole.getToolflowUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        userRoleRepository.delete(entity);
    }

    @Override
    public List<Role> getAllRoles() {
        return Arrays.stream(Role.values())
                .collect(Collectors.toList());
    }
}
