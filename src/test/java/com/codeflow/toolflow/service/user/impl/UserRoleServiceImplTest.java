package com.codeflow.toolflow.service.user.impl;

import com.codeflow.toolflow.dto.RUserRole;
import com.codeflow.toolflow.mapper.UserRoleMapper;
import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.persistence.user.entity.UserRole;
import com.codeflow.toolflow.persistence.user.repository.UserRepository;
import com.codeflow.toolflow.persistence.user.repository.UserRoleRepository;
import com.codeflow.toolflow.util.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceImplTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleMapper userRoleMapper;

    @InjectMocks
    private UserRoleServiceImpl userRoleService;

    private RUserRole rUserRole;
    private User toolflowUser;
    private UserRole userRole;

    @BeforeEach
    void setUp() {
        rUserRole = new RUserRole();
        rUserRole.setId(1L);
        rUserRole.setToolflowUserId(100L);
        rUserRole.setRole(Role.ADMINISTRATOR);

        toolflowUser = new User();
        toolflowUser.setId(100L);
        toolflowUser.setUsername("admin");
        toolflowUser.setPassword("secret"); // luego se limpiará

        userRole = new UserRole();
        userRole.setId(1L);
        userRole.setToolflowUser(toolflowUser);
        userRole.setRole(Role.ADMINISTRATOR);
    }

    @Test
    void create_whenValidInput_shouldSaveAndReturnUserRoleWithNullPassword() {
        // Arrange
        when(userRoleMapper.fromDto(rUserRole)).thenReturn(userRole);
        when(userRepository.findById(rUserRole.getToolflowUserId())).thenReturn(Optional.of(toolflowUser));
        when(userRoleRepository.save(userRole)).thenReturn(userRole);

        // Act
        UserRole result = userRoleService.create(rUserRole);

        // Assert
        assertNotNull(result);
        assertEquals(userRole.getId(), result.getId());
        assertEquals(Role.ADMINISTRATOR, result.getRole());
        assertNotNull(result.getToolflowUser());
        assertNull(result.getToolflowUser().getPassword()); // Password debe ser limpiado

        verify(userRoleMapper).fromDto(rUserRole);
        verify(userRepository).findById(rUserRole.getToolflowUserId());
        verify(userRoleRepository).save(userRole);
    }

    @Test
    void delete_whenValidInput_shouldDeleteUserRole() {
        // Arrange
        when(userRoleMapper.fromDto(rUserRole)).thenReturn(userRole);
        when(userRepository.findById(rUserRole.getToolflowUserId())).thenReturn(Optional.of(toolflowUser));

        // Act
        userRoleService.delete(rUserRole);

        // Assert
        verify(userRoleMapper).fromDto(rUserRole);
        verify(userRepository).findById(rUserRole.getToolflowUserId());
        verify(userRoleRepository).delete(userRole);
    }

    @Test
    void delete_whenUserNotFound_shouldThrowException() {
        // Arrange
        when(userRoleMapper.fromDto(rUserRole)).thenReturn(userRole);
        when(userRepository.findById(rUserRole.getToolflowUserId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userRoleService.delete(rUserRole);
        });

        assertEquals("User not found", exception.getMessage());

        verify(userRoleMapper).fromDto(rUserRole);
        verify(userRepository).findById(rUserRole.getToolflowUserId());
        verify(userRoleRepository, never()).delete(any());
    }

    @Test
    void getAllRoles_shouldReturnAllRoles() {
        // Act
        List<Role> roles = userRoleService.getAllRoles();

        // Assert
        assertNotNull(roles);
        assertEquals(Role.values().length, roles.size());
        assertTrue(roles.containsAll(List.of(Role.values())));
    }
}