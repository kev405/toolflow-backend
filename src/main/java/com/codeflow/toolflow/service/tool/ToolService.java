package com.codeflow.toolflow.service.tool;

import com.codeflow.toolflow.dto.tool.ToolRequest;
import com.codeflow.toolflow.dto.tool.ToolResponse;
import com.codeflow.toolflow.dto.tool.ToolStockRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface defining tool-related services.
 */
public interface ToolService {

    /**
     * Registers a new tool in the system.
     *
     * @param toolRequest DTO with tool data.
     * @return the created ToolResponse.
     */
    ToolResponse registerOneTool(ToolRequest toolRequest);

    /**
     * Updates an existing tool.
     *
     * @param id          the ID of the tool.
     * @param toolRequest updated data.
     * @return updated ToolResponse.
     */
    ToolResponse updateOneTool(Long id, ToolRequest toolRequest);

    /**
     * Retrieves a single tool by ID.
     *
     * @param id the tool's ID.
     * @return ToolResponse.
     */
    ToolResponse getOne(Long id);

    /**
     * Deletes (soft delete) a tool.
     *
     * @param id ID of the tool to delete.
     */
    void deleteOneTool(Long id);

    /**
     * Returns a paginated list of tools.
     *
     * @param pageable pagination and sorting configuration
     * @param filters  list of filter expressions in the format {@code column:value1,value2,...}
     * @return a {@link Page} of {@link ToolResponse} objects matching the criteria
     */
    Page<ToolResponse> getPage(Pageable pageable, List<String> filters);

    ToolResponse updateStock(Long id, ToolStockRequest toolStockRequest);
}
