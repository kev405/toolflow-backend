package com.codeflow.toolflow.service.user.impl;

import com.codeflow.toolflow.dto.user.UserRequest;
import com.codeflow.toolflow.dto.user.UserResponse;
import com.codeflow.toolflow.service.user.UserService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.persistence.user.entity.UserRole;
import com.codeflow.toolflow.persistence.user.repository.UserRepository;
import com.codeflow.toolflow.persistence.user.repository.UserRoleRepository;
import com.codeflow.toolflow.util.exception.InvalidPasswordException;

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
     * @throws InvalidPasswordException if the password does not meet the required criteria.
     * @throws RuntimeException if there is an issue saving the user or user role to the database.
     */
    @Override
    public UserResponse registerOneCustomer(UserRequest userRequest) {
        isValidPassword(userRequest);

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

        UserRole userRole = UserRole.builder()
                .role(userRequest.getRole())
                .toolflowUser(userSaved)
                .createdAt(userSaved.getCreatedAt())
                .createdBy(userSaved.getCreatedBy())
                .build();

        userRoleRepository.save(userRole);

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userSaved.getId());
        userResponse.setName(userSaved.getName());
        userResponse.setLastName(userSaved.getLastName());
        userResponse.setEmail(userSaved.getEmail());
        userResponse.setPhone(userResponse.getPhone());
        userResponse.setUsername(userSaved.getUsername());
        userResponse.setRole(userRequest.getRole());

        return userResponse;
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user to be found.
     * @return an {@code Optional<User>} containing the found user, or {@code Optional.empty()}
     *         if no user is found with the given username.
     */
    @Override
    public Optional<User> findOneByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Validates that the password and repeated password provided in the {@code UserRequest}
     * are non-empty and match each other.
     *
     * @param userRequest the request object containing the user's password information.
     * @throws InvalidPasswordException if the passwords are empty or do not match.
     */
    private void isValidPassword(UserRequest userRequest) {

        if(!StringUtils.hasText(userRequest.getPassword()) || !StringUtils.hasText(userRequest.getRepeatedPassword())){
            throw new InvalidPasswordException("Passwords don't match");
        }

        if(!userRequest.getPassword().equals(userRequest.getRepeatedPassword())){
            throw new InvalidPasswordException("Passwords don't match");
        }
    }
}
