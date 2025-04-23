package com.codeflow.toolflow.service.category;

import com.codeflow.toolflow.dto.category.CategoryResponse;
import com.codeflow.toolflow.persistence.category.entity.Category;

import java.util.List;

/**
 * Service interface for handling category-related operations.
 */
public interface CategoryService {

    /**
     * Finds a category by its name or creates a new one if it doesn't exist.
     *
     * @param name the name of the category to look for or create
     * @return a {@link Category} entity, either existing or newly created
     */
    Category findOrCreateByName(String name);

    /**
     * Retrieves all existing categories in the system.
     *
     * @return a list of {@link CategoryResponse} objects representing each category
     */
    List<CategoryResponse> getAll();
}
