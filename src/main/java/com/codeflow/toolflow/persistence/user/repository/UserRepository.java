package com.codeflow.toolflow.persistence.user.repository;

import com.codeflow.toolflow.persistence.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

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
}
