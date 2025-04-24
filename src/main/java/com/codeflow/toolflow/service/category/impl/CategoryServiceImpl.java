package com.codeflow.toolflow.service.category.impl;

import com.codeflow.toolflow.dto.auth.UserLogin;
import com.codeflow.toolflow.dto.category.CategoryResponse;
import com.codeflow.toolflow.mapper.category.CategoryMapper;
import com.codeflow.toolflow.persistence.category.entity.Category;
import com.codeflow.toolflow.persistence.category.repository.CategoryRepository;
import com.codeflow.toolflow.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation for category-related operations.
 * <p>
 * This service is responsible for resolving existing categories by name (case-insensitive),
 * or creating new ones if they don't exist, including setting audit metadata automatically.
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Finds an existing category by name, ignoring case. If no category is found,
     * creates a new one with the current authenticated user as the creator and updater.
     *
     * @param name the name of the category to find or create
     * @return the resolved or newly created {@link Category}
     */
    @Override
    public Category findOrCreateByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Long userId = getCurrentUserId();
                    Category newCategory = Category.builder()
                            .name(name)
                            .status(true)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .createdBy(userId)
                            .updatedBy(userId)
                            .build();
                    return categoryRepository.save(newCategory);
                });
    }

    /**
     * Retrieves all categories currently stored in the system.
     * <p>
     * Each category is mapped to a {@link CategoryResponse} DTO to decouple
     * the internal entity representation from the API response.
     *
     * @return a list of {@link CategoryResponse} objects representing all categories
     */
    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    /**
     * Retrieves the ID of the current authenticated user from the security context.
     *
     * @return the user ID of the authenticated user
     * @throws IllegalStateException if no authenticated user is present in the security context
     */
    private Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserLogin userDetails) {
            return userDetails.getId();
        }
        throw new IllegalStateException("No authenticated user found.");
    }
}

