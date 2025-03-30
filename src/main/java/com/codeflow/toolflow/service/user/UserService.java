package com.codeflow.toolflow.service.user;

import java.util.Optional;

import com.codeflow.toolflow.dto.user.UserRequest;
import com.codeflow.toolflow.dto.user.UserResponse;
import com.codeflow.toolflow.persistence.user.entity.User;

/**
 * Interface defining user-related services.
 */
public interface UserService {

    /**
     * Registers a new customer using the provided {@code UserRequest} information.
     *
     * @param userRequest the object containing the user data to be registered.
     * @return a {@code UserResponse} object containing the result of the registration.
     */
    UserResponse registerOneCustomer(UserRequest userRequest);

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user to find.
     * @return an {@code Optional<User>} containing the found user, or {@code Optional.empty()}
     *         if no user is found with the given username.
     */
    Optional<User> findOneByUsername(String username);
}
