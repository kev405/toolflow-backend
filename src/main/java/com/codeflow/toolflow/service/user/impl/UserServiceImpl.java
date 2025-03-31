package com.codeflow.toolflow.service.user.impl;

import com.codeflow.toolflow.dto.user.UserRequest;
import com.codeflow.toolflow.dto.user.UserResponse;
import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.persistence.user.entity.UserRole;
import com.codeflow.toolflow.persistence.user.repository.UserRepository;
import com.codeflow.toolflow.persistence.user.repository.UserRoleRepository;
import com.codeflow.toolflow.service.user.UserService;
import com.codeflow.toolflow.util.enums.Role;
import com.codeflow.toolflow.util.exception.InvalidPasswordException;
import com.codeflow.toolflow.util.exception.InvalidRoleAssignmentException;
import com.codeflow.toolflow.util.exception.UserAlreadyExistsException;
import com.codeflow.toolflow.util.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
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

    private final UserRoleRepository userRoleRepository;

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

        User user = new User();
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setUsername(userRequest.getUsername());
        user.setName(userRequest.getName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(Integer.parseInt(userRequest.getPhone()));
        user.setCreatedBy(userRequest.getCreatedBy());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(userRequest.getUpdatedBy());
        user.setStatus(true);

        User userSaved = userRepository.save(user);

        List<UserRole> userRoles = new ArrayList<>();
        for (Role role : roles) {
            UserRole userRole = UserRole.builder()
                    .role(role)
                    .toolflowUser(userSaved)
                    .createdAt(userSaved.getCreatedAt())
                    .createdBy(userSaved.getCreatedBy())
                    .build();
            userRoles.add(userRole);
        }

        userRoleRepository.saveAll(userRoles);

        return buildUserResponse(userSaved, roles);
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

        List<Role> roles = areValidateRoles(userRequest.getRoles());

        existingUser.setUsername(userRequest.getUsername());
        existingUser.setName(userRequest.getName());
        existingUser.setLastName(userRequest.getLastName());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPhone(Integer.parseInt(userRequest.getPhone()));
        existingUser.setUpdatedAt(LocalDateTime.now());
        existingUser.setUpdatedBy(userRequest.getUpdatedBy());

        User userSaved = userRepository.save(existingUser);

        List<UserRole> existingUserRoles = userRoleRepository.findByToolflowUser(existingUser);
        List<Role> existingRoles = existingUserRoles.stream().map(UserRole::getRole).toList();

        if (!existingRoles.equals(roles)) {
            if (!existingUserRoles.isEmpty()) {
                userRoleRepository.deleteAll(existingUserRoles);
            }

            List<UserRole> newUserRoles = new ArrayList<>();
            for (Role role : roles) {
                UserRole userRole = UserRole.builder()
                        .role(role)
                        .toolflowUser(userSaved)
                        .createdAt(LocalDateTime.now())
                        .createdBy(userRequest.getUpdatedBy())
                        .build();
                newUserRoles.add(userRole);
            }
            userRoleRepository.saveAll(newUserRoles);
        }

        return buildUserResponse(userSaved, userRequest.getRoles());
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
    private List<Role> areValidateRoles(List<Role> roles) {
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
     * Constructs a UserResponse object by populating it with details from the provided
     * User object and list of roles.
     *
     * @param user  the user object containing user details such as ID, username, name, etc.
     * @param roles the list of roles assigned to the user
     * @return a UserResponse object populated with user and role information
     */
    private UserResponse buildUserResponse(User user, List<Role> roles) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(String.valueOf(user.getPhone()));
        response.setRoles(roles);
        return response;
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
     * Retrieves a user by id or throws an exception if not found.
     *
     * @param id the user's id.
     * @return the found User.
     * @throws UserNotFoundException if user is not found
     */
    private User findOneById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

}
