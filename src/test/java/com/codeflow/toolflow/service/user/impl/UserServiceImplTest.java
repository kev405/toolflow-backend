package com.codeflow.toolflow.service.user.impl;

import com.codeflow.toolflow.dto.auth.UserLogin;
import com.codeflow.toolflow.dto.user.UserRequest;
import com.codeflow.toolflow.dto.user.UserResponse;
import com.codeflow.toolflow.mapper.user.UserMapper;
import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.persistence.user.entity.UserRole;
import com.codeflow.toolflow.persistence.user.repository.UserRepository;
import com.codeflow.toolflow.util.enums.Role;
import com.codeflow.toolflow.util.exception.InvalidPasswordException;
import com.codeflow.toolflow.util.exception.InvalidRoleAssignmentException;
import com.codeflow.toolflow.util.exception.UserAlreadyExistsException;
import com.codeflow.toolflow.util.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        UserLogin userLogin = new UserLogin();
        userLogin.setId(1L);
        userLogin.setUsername("mockUser");
        userLogin.setRoles(List.of(Role.ADMINISTRATOR.name()));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                userLogin, null
        ));
    }

    @Test
    void registerOneUser_success() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setUsername("johndoe");
        request.setPassword("password123");
        request.setRepeatedPassword("password123");
        request.setName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPhone("1234567890");
        request.setRoles(List.of(Role.ADMINISTRATOR));

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded123");

        User user = new User();
        user.setUsername("johndoe");
        user.setPassword("encoded123");
        user.setUserRoles(new ArrayList<>());
        when(userMapper.toEntity(request)).thenReturn(user);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("johndoe");
        savedUser.setUserRoles(List.of(UserRole.builder().role(Role.ADMINISTRATOR).build()));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(1L);
        expectedResponse.setUsername("johndoe");
        expectedResponse.setRoles(List.of(Role.ADMINISTRATOR));
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        // Act
        var response = userService.registerOneUser(request);

        // Assert
        assertNotNull(response);
        assertEquals("johndoe", response.getUsername());
        assertTrue(response.getRoles().contains(Role.ADMINISTRATOR));
    }

    @Test
    void registerOneUser_shouldThrowException_whenUsernameAlreadyExists() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setUsername("existingUser");
        request.setPassword("Password123!");
        request.setRepeatedPassword("Password123!");
        request.setRoles(Collections.singletonList(Role.ADMINISTRATOR));

        when(userRepository.findByUsername("existingUser"))
                .thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerOneUser(request);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateOneUser_success() {
        // Arrange
        Long userId = 1L;

        UserRequest request = UserRequest.builder()
                .username("updatedUser")
                .password("SecurePass123")
                .repeatedPassword("SecurePass123")
                .name("Updated")
                .lastName("User")
                .email("updated@example.com")
                .phone("987654321")
                .roles(List.of(Role.ADMINISTRATOR))
                .build();

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldUser");
        existingUser.setName("Old");
        existingUser.setLastName("User");
        existingUser.setEmail("old@example.com");
        existingUser.setPhone(123456789);
        existingUser.setStatus(true);
        existingUser.setUserRoles(new ArrayList<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("SecurePass123")).thenReturn("encoded123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(userId);
        expectedResponse.setUsername("updatedUser");
        expectedResponse.setEmail("updated@example.com");
        expectedResponse.setRoles(List.of(Role.ADMINISTRATOR));
        when(userMapper.toResponse(any(User.class))).thenReturn(expectedResponse);

        // Act
        UserResponse response = userService.updateOneUser(userId, request);

        // Assert
        assertNotNull(response);
        assertEquals(request.getUsername(), response.getUsername());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getRoles(), response.getRoles());

        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteOneUser_success() {
        // Arrange
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setStatus(true); // asumimos que está activo

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        userService.deleteOneUser(userId);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertFalse(savedUser.isStatus());
    }

    @Test
    void getPage_success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        String search = "admin";
        String searchColumn = "username";

        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setName("Admin");
        user.setLastName("Istrador");
        user.setEmail("admin@example.com");
        user.setPhone(123456789);
        user.setUserRoles(new ArrayList<>());

        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(userPage);

        UserResponse mapped = new UserResponse();
        mapped.setId(1L);
        mapped.setUsername("admin");
        mapped.setName("Admin");
        when(userMapper.toResponse(user)).thenReturn(mapped);

        // Act
        Page<UserResponse> result = userService.getPage(pageable, search, searchColumn);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("admin", result.getContent().get(0).getUsername());

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getPage_withoutSearchParams_returnsAllUsers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        String search = ""; // o null
        String searchColumn = ""; // o null

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(userPage);

        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("user1");

        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Act
        Page<UserResponse> result = userService.getPage(pageable, search, searchColumn);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("user1", result.getContent().get(0).getUsername());

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getOne_success() {
        // Arrange
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("admin");
        user.setName("Admin");
        user.setLastName("Istrador");
        user.setEmail("admin@example.com");
        user.setPhone(123456789);
        user.setUserRoles(new ArrayList<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(userId);
        expectedResponse.setUsername("admin");
        expectedResponse.setName("Admin");

        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        // Act
        UserResponse result = userService.getOne(userId);

        // Assert
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals("Admin", result.getName());
        verify(userRepository).findById(userId);
        verify(userMapper).toResponse(user);
    }

    @Test
    void getOne_userNotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getOne(userId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void registerOneUser_invalidPassword_throwsException() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setUsername("newuser");
        request.setPassword("123"); // contraseña inválida (por ejemplo, demasiado corta)
        request.setEmail("newuser@example.com");
        request.setName("New");
        request.setLastName("User");

        // Act & Assert
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            userService.registerOneUser(request);
        });

        assertEquals("Passwords don't match", exception.getMessage()); // depende del mensaje que tú lances
    }

    @Test
    void registerOneUser_passwordsDoNotMatch_throwsException() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setUsername("newuser");
        request.setPassword("Password123");
        request.setRepeatedPassword("DifferentPassword"); // <- no coinciden
        request.setRoles(Collections.singletonList(Role.ADMINISTRATOR));

        // Act & Assert
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            userService.registerOneUser(request);
        });

        assertEquals("Passwords don't match", exception.getMessage());
    }

    @Test
    void registerOneUser_validPassword_registersSuccessfully() {
        UserRequest request = new UserRequest();
        request.setUsername("validuser");
        request.setPassword("SecurePass123");
        request.setRepeatedPassword("SecurePass123");
        request.setName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPhone("123456789");
        request.setRoles(Collections.singletonList(Role.ADMINISTRATOR));

        when(userRepository.findByUsername("validuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("SecurePass123")).thenReturn("encoded-password");

        User mappedUser = new User();
        mappedUser.setUsername("validuser");
        mappedUser.setPassword("encoded-password");
        mappedUser.setName("John");
        mappedUser.setLastName("Doe");
        mappedUser.setEmail("john.doe@example.com");
        mappedUser.setPhone(123456789);
        mappedUser.setUserRoles(new ArrayList<>());

        when(userMapper.toEntity(request)).thenReturn(mappedUser);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("validuser");
        savedUser.setName("John");
        savedUser.setLastName("Doe");
        savedUser.setEmail("john.doe@example.com");
        savedUser.setPhone(123456789);
        savedUser.setUserRoles(List.of(UserRole.builder().role(Role.ADMINISTRATOR).build()));

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(1L);
        expectedResponse.setUsername("validuser");
        expectedResponse.setName("John");
        expectedResponse.setLastName("Doe");
        expectedResponse.setEmail("john.doe@example.com");
        expectedResponse.setPhone("123456789");
        expectedResponse.setRoles(List.of(Role.ADMINISTRATOR));

        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse response = userService.registerOneUser(request);

        assertNotNull(response);
        assertEquals("validuser", response.getUsername());
        assertEquals("John", response.getName());
        assertEquals("Doe", response.getLastName());
        assertEquals("123456789", response.getPhone());
        assertEquals(1, response.getRoles().size());
        assertTrue(response.getRoles().contains(Role.ADMINISTRATOR));

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerOneUser_blankPasswords_throwsException() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setUsername("newuser");
        request.setPassword(""); // vacía
        request.setRepeatedPassword(""); // vacía también
        request.setRoles(Collections.singletonList(Role.ADMINISTRATOR));

        // Act & Assert
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            userService.registerOneUser(request);
        });

        assertEquals("Passwords don't match", exception.getMessage());
    }

    @Test
    void areValidateRoles_withStudentAndOtherRole_shouldThrowException() {
        // Arrange
        List<Role> roles = List.of(Role.STUDENT, Role.TEACHER);

        // Act & Assert
        InvalidRoleAssignmentException exception = assertThrows(
                InvalidRoleAssignmentException.class,
                () -> userService.areValidateRoles(roles)
        );

        assertEquals("A student user can only have the STUDENT role exclusively", exception.getMessage());
    }

    @Test
    void testAreValidateRoles_withNullRole_shouldNotThrowException() {
        // Arrange
        List<Role> roles = new ArrayList<>();
        roles.add(Role.ADMINISTRATOR);
        roles.add(null);

        // Act
        List<Role> result = userService.areValidateRoles(roles);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains(null)); // porque no se filtran
    }

    @Test
    void testGetOne_shouldReturnCorrectUserResponse() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("johndoe");
        user.setName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPhone(123456789);

        UserRole userRole = new UserRole();
        userRole.setRole(Role.STUDENT);
        user.setUserRoles(List.of(userRole));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse mockedResponse = new UserResponse();
        mockedResponse.setId(1L);
        mockedResponse.setUsername("johndoe");
        mockedResponse.setName("John");
        mockedResponse.setLastName("Doe");
        mockedResponse.setEmail("john.doe@example.com");
        mockedResponse.setPhone("123456789");
        mockedResponse.setRoles(List.of(Role.STUDENT));

        when(userMapper.toResponse(user)).thenReturn(mockedResponse);

        // Act
        UserResponse response = userService.getOne(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("johndoe", response.getUsername());
        assertEquals("John", response.getName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals("123456789", response.getPhone());
        assertEquals(List.of(Role.STUDENT), response.getRoles());
    }

    @Test
    void testFindOneByUsername_shouldReturnUser() {
        // Arrange
        String username = "coolUser";
        User mockUser = new User();
        mockUser.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act
        Optional<User> result = userService.findOneByUsername(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
    }

    @Test
    void testFindOneById_shouldReturnUser() throws Exception {
        // Arrange
        Long userId = 42L;
        User mockUser = new User();
        mockUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        Method method = UserServiceImpl.class.getDeclaredMethod("findOneById", Long.class);
        method.setAccessible(true);

        // Act
        User result = (User) method.invoke(userService, userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void testFindOneById_userNotFound_shouldThrowException() throws Exception {
        // Arrange
        Long userId = 404L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Method method = UserServiceImpl.class.getDeclaredMethod("findOneById", Long.class);
        method.setAccessible(true);

        // Act & Assert
        assertThrows(InvocationTargetException.class, () -> {
            try {
                method.invoke(userService, userId);
            } catch (InvocationTargetException e) {
                throw e;
            }
        }, "Should throw UserNotFoundException");
    }
}