package com.codeflow.toolflow.persistence.user.repository;

import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.util.exception.InvalidSearchColumnException;
import org.springframework.data.jpa.domain.Specification;
import java.util.Arrays;

/**
 * Utility class containing specifications to be used with the {@link User} entity for dynamic query building.
 * This class provides methods to create {@link Specification} for filtering
 * and searching User entities in a database.
 */
public class UserSpecifications {

    /**
     * Creates a specification to filter users based on their active status.
     * This method generates a condition where the user's status is true.
     *
     * @return a {@link Specification} for filtering active {@link User} entities
     */
    public static Specification<User> userIsActive() {
        return (root, query, cb) -> cb.equal(root.get("status"), true);
    }

    /**
     * Creates a {@link Specification} to perform a search on a specific column of the {@link User} entity.
     * The column is matched against the provided search term using a case-insensitive "LIKE" operation.
     *
     * @param column the column name to search (valid options: "id", "username", "name", "last_name", "email")
     * @param search the search term to match against the specified column
     * @return a {@link Specification} that can be used to perform the search operation
     * @throws InvalidSearchColumnException if the provided column name is not a valid searchable column
     */
    public static Specification<User> searchByColumn(String column, String search) {
        if (!Arrays.asList("id", "username", "name", "last_name", "email").contains(column.toLowerCase())) {
            throw new InvalidSearchColumnException("Invalid column name: " + column);
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get(column)), "%" + search.toLowerCase() + "%");
    }
}

