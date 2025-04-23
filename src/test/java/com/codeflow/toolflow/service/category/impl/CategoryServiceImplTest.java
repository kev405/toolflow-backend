package com.codeflow.toolflow.service.category.impl;

import com.codeflow.toolflow.dto.auth.UserLogin;
import com.codeflow.toolflow.dto.category.CategoryResponse;
import com.codeflow.toolflow.mapper.category.CategoryMapper;
import com.codeflow.toolflow.persistence.category.entity.Category;
import com.codeflow.toolflow.persistence.category.repository.CategoryRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    private UserLogin mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new UserLogin();
        mockUser.setId(1L);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser, null)
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Category mockCategory(String name) {
        return Category.builder()
                .id(1L)
                .name(name)
                .status(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(1L)
                .updatedBy(1L)
                .build();
    }

    @Test
    void shouldReturnExistingCategoryByName() {
        Category existing = mockCategory("Hardware");

        when(categoryRepository.findByNameIgnoreCase("Hardware"))
                .thenReturn(Optional.of(existing));

        Category result = categoryService.findOrCreateByName("Hardware");

        assertThat(result).isEqualTo(existing);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldCreateNewCategoryIfNotFound() {
        when(categoryRepository.findByNameIgnoreCase("Software")).thenReturn(Optional.empty());

        Category saved = mockCategory("Software");

        when(categoryRepository.save(any())).thenReturn(saved);

        Category result = categoryService.findOrCreateByName("Software");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Software");
        assertThat(result.getCreatedBy()).isEqualTo(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void shouldReturnAllCategoriesMapped() {
        List<Category> entities = List.of(
                mockCategory("Hardware"),
                mockCategory("Software")
        );
        List<CategoryResponse> responses = List.of(
                CategoryResponse.builder()
                        .id(1L)
                        .name("Hardware")
                        .status(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                CategoryResponse.builder()
                        .id(2L)
                        .name("Software")
                        .status(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        when(categoryRepository.findAll()).thenReturn(entities);
        when(categoryMapper.toResponse(entities.get(0))).thenReturn(responses.get(0));
        when(categoryMapper.toResponse(entities.get(1))).thenReturn(responses.get(1));

        List<CategoryResponse> result = categoryService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Hardware");
        assertThat(result.get(1).getName()).isEqualTo("Software");
    }

    @Test
    void shouldThrowIfNoAuthenticatedUser() {
        SecurityContextHolder.clearContext(); // No auth context

        CategoryServiceImpl service = new CategoryServiceImpl(categoryRepository, categoryMapper);

        assertThatThrownBy(() -> {
            service.findOrCreateByName("Test");
        }).isInstanceOf(IllegalStateException.class)
                .hasMessage("No authenticated user found.");
    }

    @Test
    void shouldThrowIfPrincipalIsNotUserLogin() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymous", null)
        );

        CategoryServiceImpl service = new CategoryServiceImpl(categoryRepository, categoryMapper);

        assertThatThrownBy(() -> service.findOrCreateByName("Any"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No authenticated user found.");
    }
}
