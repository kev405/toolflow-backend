package com.codeflow.toolflow.service.tool.impl;

import com.codeflow.toolflow.dto.auth.UserLogin;
import com.codeflow.toolflow.dto.tool.ToolRequest;
import com.codeflow.toolflow.dto.tool.ToolResponse;
import com.codeflow.toolflow.dto.tool.ToolStockRequest;
import com.codeflow.toolflow.mapper.tool.ToolMapper;
import com.codeflow.toolflow.persistence.category.entity.Category;
import com.codeflow.toolflow.persistence.tool.entity.Tool;
import com.codeflow.toolflow.persistence.tool.repository.ToolRepository;
import com.codeflow.toolflow.service.category.CategoryService;
import com.codeflow.toolflow.util.exception.ToolNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ToolServiceImplTest {

    @InjectMocks
    private ToolServiceImpl toolService;

    @Mock
    private ToolRepository toolRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ToolMapper toolMapper;

    @Captor
    private ArgumentCaptor<Tool> toolCaptor;

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

    private Tool createMockTool() {
        Tool tool = new Tool();
        tool.setId(1L);
        tool.setAvailable(5);
        tool.setOnLoan(3);
        tool.setDamaged(2);
        tool.setQuantity(10);
        tool.setStatus(true);
        tool.setCreatedAt(LocalDateTime.now().minusDays(1));
        tool.setUpdatedAt(LocalDateTime.now());
        tool.setCreatedBy(1L);
        tool.setUpdatedBy(1L);
        return tool;
    }

    private Category createMockCategory() {
        Category category = new Category();
        category.setId(99L);
        category.setName("Mock Category");
        return category;
    }

    @Test
    void shouldRegisterTool() {
        ToolRequest request = new ToolRequest();
        Tool tool = createMockTool();
        Category category = createMockCategory();

        ToolResponse responseMock = new ToolResponse();
        responseMock.setToolName(tool.getToolName());

        when(toolMapper.toEntity(request)).thenReturn(tool);
        when(categoryService.findOrCreateByName(any())).thenReturn(category);
        when(toolRepository.save(any())).thenReturn(tool);
        when(toolMapper.toResponse(tool)).thenReturn(responseMock);

        ToolResponse response = toolService.registerOneTool(request);

        assertThat(response).isNotNull();
        assertThat(response.getToolName()).isEqualTo(tool.getToolName());

        verify(toolRepository).save(toolCaptor.capture());
        Tool captured = toolCaptor.getValue();

        assertThat(captured.getCreatedBy()).isEqualTo(1L);
        assertThat(captured.getStatus()).isTrue();
        assertThat(captured.getQuantity()).isEqualTo(captured.getAvailable());
    }

    @Test
    void shouldUpdateTool() {
        ToolRequest request = new ToolRequest();
        Tool existing = createMockTool();
        Tool updated = createMockTool();
        updated.setAvailable(10);
        updated.setOnLoan(5);
        updated.setDamaged(2);
        updated.setQuantity(17);

        Category category = createMockCategory();

        when(toolRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(toolMapper.toEntity(request)).thenReturn(updated);
        when(categoryService.findOrCreateByName(any())).thenReturn(category);
        when(toolRepository.save(any())).thenReturn(updated);
        when(toolMapper.toResponse(any())).thenReturn(new ToolResponse());

        ToolResponse response = toolService.updateOneTool(1L, request);

        assertThat(response).isNotNull();
        verify(toolRepository).save(toolCaptor.capture());
        Tool captured = toolCaptor.getValue();
        assertThat(captured.getQuantity()).isEqualTo(17);
    }

    @Test
    void shouldGetToolById() {
        Tool tool = createMockTool();
        when(toolRepository.findById(1L)).thenReturn(Optional.of(tool));
        when(toolMapper.toResponse(tool)).thenReturn(new ToolResponse());

        ToolResponse response = toolService.getOne(1L);

        assertThat(response).isNotNull();
    }

    @Test
    void shouldThrowWhenToolNotFound() {
        when(toolRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> toolService.getOne(1L))
                .isInstanceOf(ToolNotFoundException.class)
                .hasMessage("Tool not found");
    }

    @Test
    void shouldDeleteToolSoftly() {
        Tool tool = createMockTool();
        when(toolRepository.findById(1L)).thenReturn(Optional.of(tool));

        toolService.deleteOneTool(1L);

        verify(toolRepository).save(toolCaptor.capture());
        Tool captured = toolCaptor.getValue();
        assertThat(captured.getStatus()).isFalse();
        assertThat(captured.getAvailable()).isZero();
        assertThat(captured.getUpdatedBy()).isEqualTo(1L);
    }

    @Test
    void shouldReturnPageWhenFiltersAreNull() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Tool> page = new PageImpl<>(List.of(createMockTool()));
        when(toolRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(toolMapper.toResponse(any())).thenReturn(new ToolResponse());

        Page<ToolResponse> result = toolService.getPage(pageable, null);

        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldReturnPageWhenFiltersAreEmpty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Tool> page = new PageImpl<>(List.of(createMockTool()));
        when(toolRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(toolMapper.toResponse(any())).thenReturn(new ToolResponse());

        Page<ToolResponse> result = toolService.getPage(pageable, Collections.emptyList());

        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldIgnoreInvalidFilterWithoutColon() {
        Pageable pageable = PageRequest.of(0, 5);
        List<String> filters = List.of("invalidfilter");
        Page<Tool> page = new PageImpl<>(List.of(createMockTool()));

        when(toolRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(toolMapper.toResponse(any())).thenReturn(new ToolResponse());

        Page<ToolResponse> result = toolService.getPage(pageable, filters);

        assertThat(result).isNotEmpty(); // still runs, just skips that invalid filter
    }

    @Test
    void shouldReturnEmptyPageIfNoToolsFound() {
        Pageable pageable = PageRequest.of(0, 5);
        List<String> filters = List.of("status:true");
        Page<Tool> emptyPage = new PageImpl<>(Collections.emptyList());

        when(toolRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(emptyPage);

        Page<ToolResponse> result = toolService.getPage(pageable, filters);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldGetPageWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Tool tool = createMockTool();
        Page<Tool> page = new PageImpl<>(List.of(tool));

        when(toolRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(toolMapper.toResponse(tool)).thenReturn(new ToolResponse());

        Page<ToolResponse> result = toolService.getPage(pageable, List.of("status:true"));

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldThrowWhenUpdatingStockAndToolNotFound() {
        when(toolRepository.findById(1L)).thenReturn(Optional.empty());

        ToolStockRequest request = new ToolStockRequest();

        assertThatThrownBy(() -> toolService.updateStock(1L, request))
                .isInstanceOf(ToolNotFoundException.class)
                .hasMessage("Tool1 not found");
    }

    @Test
    void shouldUpdateStockWhenAllFieldsAreNull() {
        Tool tool = new Tool();
        tool.setAvailable(null);
        tool.setDamaged(null);
        tool.setOnLoan(null);

        ToolStockRequest request = new ToolStockRequest(); // todos los campos null

        when(toolRepository.findById(1L)).thenReturn(Optional.of(tool));
        when(toolRepository.save(any())).thenReturn(tool);
        when(toolMapper.toResponse(tool)).thenReturn(new ToolResponse());

        ToolResponse response = toolService.updateStock(1L, request);

        assertThat(response).isNotNull();
        verify(toolRepository).save(toolCaptor.capture());
        assertThat(toolCaptor.getValue().getQuantity()).isEqualTo(0); // total 0
    }

    @Test
    void shouldUpdateOnlyAvailableIfOthersAreZero() {
        Tool tool = new Tool();
        tool.setAvailable(0);
        tool.setDamaged(0);
        tool.setOnLoan(0);

        ToolStockRequest request = new ToolStockRequest();
        request.setAvailable(8);
        when(toolRepository.findById(1L)).thenReturn(Optional.of(tool));
        when(toolRepository.save(any())).thenReturn(tool);
        when(toolMapper.toResponse(tool)).thenReturn(new ToolResponse());

        ToolResponse response = toolService.updateStock(1L, request);

        assertThat(response).isNotNull();
        verify(toolRepository).save(toolCaptor.capture());
        Tool saved = toolCaptor.getValue();
        assertThat(saved.getAvailable()).isEqualTo(8);
        assertThat(saved.getDamaged()).isEqualTo(0); // corregido
        assertThat(saved.getOnLoan()).isEqualTo(0);  // corregido
        assertThat(saved.getQuantity()).isEqualTo(8);
    }

    @Test
    void shouldUpdateStock() {
        Tool tool = createMockTool();
        ToolStockRequest stockRequest = new ToolStockRequest();
        stockRequest.setAvailable(7);
        stockRequest.setDamaged(2);
        stockRequest.setOnLoan(1);

        when(toolRepository.findById(1L)).thenReturn(Optional.of(tool));
        when(toolRepository.save(any())).thenReturn(tool);
        when(toolMapper.toResponse(tool)).thenReturn(new ToolResponse());

        ToolResponse response = toolService.updateStock(1L, stockRequest);

        assertThat(response).isNotNull();
        verify(toolRepository).save(toolCaptor.capture());
        Tool captured = toolCaptor.getValue();
        assertThat(captured.getQuantity()).isEqualTo(10);
        assertThat(captured.getAvailable()).isEqualTo(7);
    }

    @Test
    void shouldThrowWhenNoAuthenticatedUserFound() {
        SecurityContextHolder.clearContext(); // Limpia el contexto para simular sesión vacía

        ToolServiceImpl serviceWithoutUser = new ToolServiceImpl(
                toolRepository, categoryService, toolMapper
        );

        assertThatThrownBy(serviceWithoutUser::getCurrentUserId)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No authenticated user found.");
    }

    @Test
    void shouldThrowWhenPrincipalIsNotUserLogin() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("not-a-userlogin", null)
        );

        ToolServiceImpl service = new ToolServiceImpl(
                toolRepository, categoryService, toolMapper
        );

        assertThatThrownBy(service::getCurrentUserId)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No authenticated user found.");
    }

    @Test
    void shouldUpdateOnlyDamagedIfOthersAreZero() {
        Tool tool = new Tool();
        tool.setAvailable(0); // no null
        tool.setDamaged(0);
        tool.setOnLoan(0);

        ToolStockRequest request = new ToolStockRequest();
        request.setDamaged(4); // solo este se actualiza

        when(toolRepository.findById(1L)).thenReturn(Optional.of(tool));
        when(toolRepository.save(any())).thenReturn(tool);
        when(toolMapper.toResponse(tool)).thenReturn(new ToolResponse());

        ToolResponse response = toolService.updateStock(1L, request);

        assertThat(response).isNotNull();
        verify(toolRepository).save(toolCaptor.capture());
        Tool saved = toolCaptor.getValue();
        assertThat(saved.getDamaged()).isEqualTo(4);
        assertThat(saved.getAvailable()).isEqualTo(0); // ahora 0 en lugar de null
        assertThat(saved.getOnLoan()).isEqualTo(0);    // ahora 0 en lugar de null
        assertThat(saved.getQuantity()).isEqualTo(4);  // suma total
    }


    @Test
    void shouldUpdateOnlyOnLoanIfOthersAreZero() {
        Tool tool = new Tool();
        tool.setAvailable(0);
        tool.setDamaged(0);
        tool.setOnLoan(0);

        ToolStockRequest request = new ToolStockRequest();
        request.setOnLoan(5);

        when(toolRepository.findById(1L)).thenReturn(Optional.of(tool));
        when(toolRepository.save(any())).thenReturn(tool);
        when(toolMapper.toResponse(tool)).thenReturn(new ToolResponse());

        ToolResponse response = toolService.updateStock(1L, request);

        assertThat(response).isNotNull();
        verify(toolRepository).save(toolCaptor.capture());
        Tool saved = toolCaptor.getValue();
        assertThat(saved.getOnLoan()).isEqualTo(5);
        assertThat(saved.getAvailable()).isEqualTo(0);
        assertThat(saved.getDamaged()).isEqualTo(0);
        assertThat(saved.getQuantity()).isEqualTo(5);
    }
}
