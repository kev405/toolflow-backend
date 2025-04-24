package com.codeflow.toolflow.persistence.category.repository;

import com.codeflow.toolflow.persistence.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Category} entities in the database.
 * <p>
 * This interface provides standard JPA-based operations such as saving, deleting,
 * and finding categories. It also includes custom query methods for specific business needs.
 * <p>
 * It extends {@link JpaRepository} to gain access to built-in JPA functionality,
 * such as pagination and sorting.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a category by its name, ignoring case sensitivity.
     * <p>
     * This method is useful when matching category names regardless of how the text is capitalized.
     * For example, searching "Electrical Tools" will match "electrical tools", "ELECTRICAL TOOLS", etc.
     *
     * @param name the name of the category to search for; must not be null
     * @return an {@link Optional} containing the matching {@link Category} if found, or empty otherwise
     */
    Optional<Category> findByNameIgnoreCase(String name);
}
