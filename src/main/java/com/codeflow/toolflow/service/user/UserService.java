package com.codeflow.toolflow.service.user;

import com.codeflow.toolflow.dto.user.UserRequest;
import com.codeflow.toolflow.dto.user.UserResponse;
import com.codeflow.toolflow.persistence.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

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
    UserResponse registerOneUser(UserRequest userRequest);


    /**
     * Updates the details of an existing user based on the provided user ID and request object.
     *
     * @param id          the unique identifier of the user to be updated.
     * @param userRequest the object containing the updated user data.
     * @return a {@code UserResponse} object containing the updated user information.
     */
    UserResponse updateOneUser(Long id, UserRequest userRequest);

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user to find.
     * @return an {@code Optional<User>} containing the found user, or {@code Optional.empty()}
     * if no user is found with the given username.
     */
    Optional<User> findOneByUsername(String username);

    /**
     * Deletes a user from the system based on the provided user ID.
     *
     * @param id the unique identifier of the user to be deleted.
     */
    void deleteOneUser(Long id);

    /**
     * Retrieves a paginated list of users based on the provided pagination, search criteria,
     * and the specific column to search within.
     *
     * @param pageable     the pagination and sorting information for the request.
     * @param search       the search keyword to filter users.
     * @param searchColumn the specific column to apply the search filter.
     * @return a {@code Page<UserResponse>} containing the paginated list of users matching the search criteria.
     */
    Page<UserResponse> getPage(Pageable pageable, String search, String searchColumn);

    /**
     * Retrieves a single user by their unique identifier.
     *
     * @param id the unique identifier of the user to be retrieved.
     * @return a {@code UserResponse} representing the details of the retrieved user.
     */
    UserResponse getOne(Long id);
}
