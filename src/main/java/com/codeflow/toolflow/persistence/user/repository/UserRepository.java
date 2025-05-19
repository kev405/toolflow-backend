package com.codeflow.toolflow.persistence.user.repository;

import com.codeflow.toolflow.persistence.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities in the database.
 * This interface provides methods to perform CRUD operations, pagination, and custom queries
 * on the {@link User} entity.
 * It extends {@link JpaRepository} for basic JPA functionalities and {@link JpaSpecificationExecutor}
 * for dynamic query construction using specifications.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * Finds a user by their username.
     *
     * @param username the username of the user to be retrieved; must not be null
     * @return an {@link Optional} containing the {@link User} object if found, or empty if no user with the provided username exists
     */
    Optional<User> findByUsername(String username);

    /**
     * Gets a list of active users based on their role.
     *
     * @param role the role to filter users by; must not be null
     * @return a list of {@link User} objects that are active and have the specified role
     */
    @Query("SELECT u FROM User u JOIN u.userRoles ur WHERE ur.role = :role AND u.status = true")
    List<User> findUsersByRole(@Param("role") com.codeflow.toolflow.util.enums.Role role);
}
