package com.codeflow.toolflow.service.user.impl;

import com.codeflow.toolflow.dto.auth.UserLogin;
import com.codeflow.toolflow.dto.user.UserRequest;
import com.codeflow.toolflow.dto.user.UserResponse;
import com.codeflow.toolflow.mapper.user.UserMapper;
import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.persistence.user.entity.UserRole;
import com.codeflow.toolflow.persistence.user.repository.UserRepository;
import com.codeflow.toolflow.persistence.user.repository.UserSpecifications;
import com.codeflow.toolflow.service.user.UserService;
import com.codeflow.toolflow.util.enums.Role;
import com.codeflow.toolflow.util.exception.InvalidPasswordException;
import com.codeflow.toolflow.util.exception.InvalidRoleAssignmentException;
import com.codeflow.toolflow.util.exception.UserAlreadyExistsException;
import com.codeflow.toolflow.util.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service implementation for user-related operations.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user in the system by validating the password,
     * creating a new `User` and `UserRole`, saving them to the database,
     * and returning a `UserResponse` with the saved user's data.
     *
     * @param userRequest The request object containing user details to be registered.
     * @return A {@link UserResponse} object containing the details of the newly registered user.
     * @throws InvalidPasswordException   if the password does not meet the required criteria.
     * @throws UserAlreadyExistsException if user already exist in the db
     * @throws RuntimeException           if there is an issue saving the user or user role to the database.
     */
    @Override
    public UserResponse registerOneUser(UserRequest userRequest) {
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        isValidPassword(userRequest);

        List<Role> roles = areValidateRoles(userRequest.getRoles());

        User user = userMapper.toEntity(userRequest);

        Long currentUserId = getCurrentUserId();
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setStatus(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy(currentUserId);
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(currentUserId);

        List<UserRole> userRoles = new ArrayList<>();
        for (Role role : roles) {
            userRoles.add(UserRole.builder()
                    .role(role)
                    .toolflowUser(user)
                    .createdAt(user.getCreatedAt())
                    .createdBy(user.getCreatedBy())
                    .build());
        }
        user.setUserRoles(userRoles);

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    /**
     * Updates a single user's details and roles in the system based on the provided user data.
     * The method retrieves the existing user by its ID, updates the user's information, and handles
     * any changes to the user's roles by validating and updating the roles as necessary.
     *
     * @param id          the unique identifier of the user to be updated
     * @param userRequest the request object containing the updated user details and roles
     * @return a {@code UserResponse} object containing the updated user details and associated roles
     */
    @Override
    public UserResponse updateOneUser(Long id, UserRequest userRequest) {
        User existingUser = findOneById(id);
        if (userRequest.getPassword() != null) {
            isValidPassword(userRequest);
        }
        List<Role> roles = areValidateRoles(userRequest.getRoles());

        existingUser.setUsername(userRequest.getUsername());
        existingUser.setName(userRequest.getName());
        existingUser.setLastName(userRequest.getLastName());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPhone(Long.valueOf(userRequest.getPhone()));
        if (userRequest.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        existingUser.setUpdatedAt(LocalDateTime.now());
        existingUser.setUpdatedBy(getCurrentUserId());

        existingUser.getUserRoles().clear();
        for (Role role : roles) {
            existingUser.getUserRoles().add(UserRole.builder()
                    .role(role)
                    .toolflowUser(existingUser)
                    .createdAt(existingUser.getUpdatedAt())
                    .createdBy(existingUser.getUpdatedBy())
                    .build());
        }

        User saved = userRepository.save(existingUser);
        return userMapper.toResponse(saved);
    }

    /**
     * Deactivates a user by setting their status to false.
     * Saves the updated user entity back to the repository.
     *
     * @param id the unique identifier of the user to be deactivated
     */
    @Override
    public void deleteOneUser(Long id) {
        User user = findOneById(id);
        user.setStatus(false);
        userRepository.save(user);
    }

    /**
     * Retrieves a paginated list of user responses based on the given pageable and search criteria.
     *
     * @param pageable     the pagination and sorting information
     * @param search       the search keyword used for filtering results
     * @param searchColumn the column to be searched with the specified keyword
     * @return a page of UserResponse objects matching the criteria
     */
    @Override
    public Page<UserResponse> getPage(Pageable pageable, String search, String searchColumn) {
        Specification<User> spec = Specification.where(UserSpecifications.userIsActive());

        if (StringUtils.hasText(search) && StringUtils.hasText(searchColumn)) {
            spec = spec.and(UserSpecifications.searchByColumn(searchColumn, search));
        }

        Page<User> users = userRepository.findAll(spec, pageable);
        return users.map(userMapper::toResponse);
    }

    /**
     * Retrieves a single user by their ID and builds a response object for the user.
     *
     * @param id the unique identifier of the user to be retrieved
     * @return a UserResponse object containing the details of the retrieved user
     */
    @Override
    public UserResponse getOne(Long id) {
        return userMapper.toResponse(findOneById(id));
    }

    /**
     * Validates that the password and repeated password provided in the {@code UserRequest}
     * are non-empty and match each other.
     *
     * @param userRequest the request object containing the user's password information.
     * @throws InvalidPasswordException if the passwords are empty or do not match.
     */
    private void isValidPassword(UserRequest userRequest) {

        if (!StringUtils.hasText(userRequest.getPassword()) || !StringUtils.hasText(userRequest.getRepeatedPassword())) {
            throw new InvalidPasswordException("Passwords don't match");
        }

        if (!userRequest.getPassword().equals(userRequest.getRepeatedPassword())) {
            throw new InvalidPasswordException("Passwords don't match");
        }
    }

    /**
     * Validates and processes the list of roles.
     *
     * @param roles the list of roles from the request.
     * @return a processed list of roles.
     * @throws InvalidRoleAssignmentException if the roles do not meet the validation criteria.
     */
    public List<Role> areValidateRoles(List<Role> roles) {
        if (roles == null) {
            throw new InvalidRoleAssignmentException("Roles cannot be null");
        }
        Set<Role> roleSet = new HashSet<>(roles);
        if (roleSet.contains(Role.STUDENT) && roleSet.size() > 1) {
            throw new InvalidRoleAssignmentException("A student user can only have the STUDENT role exclusively");
        }
        return new ArrayList<>(roleSet);
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user to be found.
     * @return an {@code Optional<User>} containing the found user, or {@code Optional.empty()}
     * if no user is found with the given username.
     */
    @Override
    public Optional<User> findOneByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all users.
     */
    @Override
    public List<UserResponse> getAllTeachers() {
        return userRepository.findUsersByRole(Role.TEACHER)
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    /**
     * Finds users by their roles.
     *
     * @param roles the list of roles to filter users by.
     * @return a list of UserResponse objects for users with the specified roles.
     */
    @Override
    public List<UserResponse> findByRoles(List<Role> roles) {
        return userRepository.findUsersByRolesIn(roles).stream()
                .map(userMapper::toResponse)
                .toList();
    }

    /**
     * Retrieves a user by id or throws an exception if not found.
     *
     * @param id the user's id.
     * @return the found User.
     * @throws UserNotFoundException if user is not found
     */
    private User findOneById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    /**
     * Retrieves the current authenticated user's ID from the security context.
     *
     * @return the user ID
     * @throws IllegalStateException if no authenticated user is found in the context
     */
    private Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserLogin userDetails) {
            return userDetails.getId();
        }
        throw new IllegalStateException("No authenticated user found.");
    }
}
