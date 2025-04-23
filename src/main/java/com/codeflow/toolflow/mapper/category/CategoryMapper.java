package com.codeflow.toolflow.mapper.category;

import com.codeflow.toolflow.dto.category.CategoryResponse;
import com.codeflow.toolflow.persistence.category.entity.Category;
import org.mapstruct.Mapper;

/**
 * Mapper for converting between {@link Category} entities and {@link CategoryResponse} DTOs.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Converts a Category entity into a CategoryResponse DTO.
     *
     * @param category the category entity to convert
     * @return the resulting CategoryResponse
     */
    CategoryResponse toResponse(Category category);
}
