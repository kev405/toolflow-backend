package com.codeflow.toolflow.service.tool.impl;

import com.codeflow.toolflow.dto.auth.UserLogin;
import com.codeflow.toolflow.dto.tool.ToolRequest;
import com.codeflow.toolflow.dto.tool.ToolResponse;
import com.codeflow.toolflow.dto.tool.ToolStockRequest;
import com.codeflow.toolflow.mapper.tool.ToolMapper;
import com.codeflow.toolflow.persistence.tool.entity.Tool;
import com.codeflow.toolflow.persistence.tool.repository.ToolRepository;
import com.codeflow.toolflow.persistence.tool.repository.ToolSpecifications;
import com.codeflow.toolflow.service.email.EmailService;
import com.codeflow.toolflow.service.category.CategoryService;
import com.codeflow.toolflow.service.tool.ToolService;
import com.codeflow.toolflow.util.exception.ToolNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for tool-related operations.
 * <p>
 * This service handles the core business logic for creating, updating, retrieving,
 * deleting, and listing tools, as well as category resolution and audit information tracking.
 */
@Service
@RequiredArgsConstructor
public class ToolServiceImpl implements ToolService {

    private final ToolRepository toolRepository;
    private final CategoryService categoryService;
    private final ToolMapper toolMapper;

    private EmailService emailService;

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Registers a new tool in the system based on the data received in the {@link ToolRequest}.
     * Automatically creates a category if it does not exist, and tracks audit information
     * for the creator.
     *
     * @param request the request object containing tool data to be saved
     * @return the created {@link ToolResponse}
     */
    @Override
    public ToolResponse registerOneTool(ToolRequest request) {
        Tool tool = mapRequestToEntity(request, new Tool());
        tool.setStatus(true);
        tool.setQuantity(Optional.ofNullable(tool.getAvailable()).orElse(0));
        tool.setDamaged(0);
        tool.setOnLoan(0);
        Tool saved = toolRepository.save(tool);
        return toolMapper.toResponse(saved);
    }

    /**
     * Updates the details of an existing tool identified by its ID.
     * The method retrieves the existing record, applies updates from the {@link ToolRequest},
     * and tracks audit metadata such as last updater and timestamp.
     *
     * @param id      the ID of the tool to be updated
     * @param request the object containing the updated tool data
     * @return the updated {@link ToolResponse}
     */
    @Override
    public ToolResponse updateOneTool(Long id, ToolRequest request) {
        Tool existing = findOneById(id);
        Tool updated = mapRequestToEntity(request, existing);
        int totalQuantity = updated.getAvailable() - updated.getDamaged() + updated.getOnLoan();
        updated.setQuantity(totalQuantity);

        Tool saved = toolRepository.save(updated);

        String email = "samuel.galindo@correounivalle.edu.co";

        if(updated.getMinimalRegistration() !=null && updated.getAvailable() != null
                && updated.getMinimalRegistration() > updated.getAvailable()) {
            emailService.sendSimpleEmail(email, "Stock Alert",
                    "The stock of tool " + updated.getToolName() + " is below the minimum registration level.");
        }
        return toolMapper.toResponse(saved);
    }

    /**
     * Retrieves a tool by its ID and returns its full details.
     *
     * @param id the unique identifier of the tool
     * @return the {@link ToolResponse} containing tool details
     * @throws ToolNotFoundException if the tool is not found
     */
    @Override
    public ToolResponse getOne(Long id) {
        return toolMapper.toResponse(findOneById(id));
    }

    /**
     * Performs a soft delete on a tool by setting its status to false.
     * This method does not remove the tool from the database.
     *
     * @param id the ID of the tool to deactivate
     */
    @Override
    public void deleteOneTool(Long id) {
        Tool tool = findOneById(id);
        tool.setStatus(false);
        tool.setAvailable(0);
        tool.setDamaged(0);
        tool.setOnLoan(0);
        tool.setUpdatedAt(LocalDateTime.now());
        tool.setUpdatedBy(getCurrentUserId());
        toolRepository.save(tool);
    }

    /**
     * Retrieves a paginated list of active tools, optionally filtered by a search query
     * and search column. The results are dynamically built using specifications.
     *
     * @param pageable the pagination and sorting information
     * @param filters  a list of search filters in the format {@code column:value1,value2,...}
     * @return a {@link Page} of {@link ToolResponse} objects matching the filters
     */
    @Override
    public Page<ToolResponse> getPage(Pageable pageable, List<String> filters) {
        Specification<Tool> spec = Specification.where(ToolSpecifications.toolIsActive());

        if (filters != null && !filters.isEmpty()) {
            for (String filter : filters) {
                String[] parts = filter.split(":");
                if (parts.length >= 2) {
                    String column = parts[0].trim();
                    String[] values = parts[1].split(",");
                    spec = spec.and(ToolSpecifications.searchByColumnValues(column, Arrays.asList(values)));
                }
            }
        }

        Page<Tool> tools = toolRepository.findAll(spec, pageable);
        return tools.map(toolMapper::toResponse);
    }

    /**
     * Updates the stock of a tool identified by its ID.
     * The method retrieves the existing record, applies updates from the {@link ToolStockRequest},
     * and recalculates the total quantity.
     *
     * @param id      the ID of the tool to be updated
     * @param request the object containing the updated stock data
     * @return the updated {@link ToolResponse}
     */
    @Override
    public ToolResponse updateStock(Long id, ToolStockRequest request) {
        Tool tool = toolRepository.findById(id)
                .orElseThrow(() -> new ToolNotFoundException("Tool" + id + " not found"));

        if (request.getAvailable() != null) {
            tool.setAvailable(request.getAvailable());
        }
        if (request.getDamaged() != null) {
            tool.setDamaged(request.getDamaged());
        }
        if (request.getOnLoan() != null) {
            tool.setOnLoan(request.getOnLoan());
        }

        int totalQuantity = (tool.getAvailable() != null ? tool.getAvailable() : 0) +
                (tool.getDamaged() != null ? tool.getDamaged() : 0) +
                (tool.getOnLoan() != null ? tool.getOnLoan() : 0);

        tool.setQuantity(totalQuantity);

        tool.setUpdatedAt(LocalDateTime.now());
        tool.setUpdatedBy(getCurrentUserId());

        String email = "samuel.galindo@correounivalle.edu.co";

        Tool updatedTool = toolRepository.save(tool);

        if(updatedTool !=null && updatedTool.getMinimalRegistration() !=null && updatedTool.getAvailable() != null
                && updatedTool.getMinimalRegistration() > updatedTool.getAvailable()) {
            emailService.sendSimpleEmail(email, "Stock Alert",
                    "The stock of tool " + updatedTool.getToolName() + " is below the minimum registration level.");
        }

        return toolMapper.toResponse(updatedTool);
    }

    /**
     * Retrieves all tools in the system.
     *
     * @return a list of {@link ToolResponse} objects representing each tool
     */
    @Override
    public List<ToolResponse> getAll() {
        return toolRepository.findAll()
                .stream()
                .map(toolMapper::toResponse)
                .toList();
    }

    /**
     * Retrieves a {@link Tool} entity by its ID.
     *
     * @param id the ID of the tool
     * @return the {@link Tool} entity
     * @throws ToolNotFoundException if the tool does not exist
     */
    private Tool findOneById(Long id) {
        return toolRepository.findById(id).orElseThrow(() -> new ToolNotFoundException("Tool not found"));
    }

    /**
     * Maps a {@link ToolRequest} DTO into a {@link Tool} entity.
     * Performs category resolution and applies auditing metadata (createdBy, updatedBy).
     *
     * @param request      the request object with input data
     * @param existingTool the existing tool (empty for new tool, filled for update)
     * @return the mapped {@link Tool} entity ready to be persisted
     */
    private Tool mapRequestToEntity(ToolRequest request, Tool existingTool) {
        Tool tool = toolMapper.toEntity(request);
        tool.setId(existingTool.getId());
        tool.setCategory(categoryService.findOrCreateByName(request.getCategory()));

        Long userId = getCurrentUserId();
        tool.setUpdatedAt(LocalDateTime.now());
        tool.setUpdatedBy(userId);

        if (tool.getId() == null) {
            tool.setCreatedAt(LocalDateTime.now());
            tool.setCreatedBy(userId);
            tool.setStatus(true);
        } else {
            tool.setStatus(existingTool.getStatus() != null ? existingTool.getStatus() : true);
            tool.setCreatedAt(existingTool.getCreatedAt());
            tool.setCreatedBy(existingTool.getCreatedBy());
        }

        return tool;
    }

    /**
     * Retrieves the current authenticated user's ID from the security context.
     *
     * @return the user ID
     * @throws IllegalStateException if no authenticated user is found in the context
     */
    Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserLogin userDetails) {
            return userDetails.getId();
        }
        throw new IllegalStateException("No authenticated user found.");
    }
}
