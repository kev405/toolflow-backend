package com.codeflow.toolflow.controller.tool;

import com.codeflow.toolflow.dto.ApiError;
import com.codeflow.toolflow.dto.tool.ToolRequest;
import com.codeflow.toolflow.dto.tool.ToolResponse;
import com.codeflow.toolflow.dto.tool.ToolStockRequest;
import com.codeflow.toolflow.service.tool.ToolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing tool operations.
 */
@RestController
@RequestMapping("/tools")
public class ToolController {

    @Autowired
    private ToolService toolService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'TOOL_ADMINISTRATOR')")
    @Operation(
            summary = "Register New Tool",
            description = "Creates a new tool with the provided tool details.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Tool registration payload. Contains data such as name, brand, quantity, availability, damaged, onLoan, consumable, etc.",
                    content = @Content(schema = @Schema(implementation = ToolRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tool successfully created", content = @Content(schema = @Schema(implementation = ToolResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<ToolResponse> registerOne(@Valid @RequestBody ToolRequest toolRequest) {
        ToolResponse response = toolService.registerOneTool(toolRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'TOOL_ADMINISTRATOR')")
    @Operation(
            summary = "Update Tool",
            description = "Updates an existing tool using the provided details.",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID of the tool to be updated", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Tool update payload.",
                    content = @Content(schema = @Schema(implementation = ToolRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tool successfully updated", content = @Content(schema = @Schema(implementation = ToolResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Tool not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<ToolResponse> updateOne(@PathVariable Long id, @Valid @RequestBody ToolRequest toolRequest) {
        ToolResponse response = toolService.updateOneTool(id, toolRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'TOOL_ADMINISTRATOR')")
    @Operation(
            summary = "Delete Tool",
            description = "Deletes (soft) a tool by marking its status as false.",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID of the tool to be deleted", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tool successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Tool not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> deleteOne(@PathVariable Long id) {
        toolService.deleteOneTool(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'TOOL_ADMINISTRATOR')")
    @Operation(
            summary = "Get Paginated Tools",
            description = "Retrieves a paginated list of tools. Supports filtering by name and multiple categories.",
            parameters = {
                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number", example = "0"),
                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size", example = "10"),
                    @Parameter(name = "sort", in = ParameterIn.QUERY, description = "Sort format: property,asc|desc", example = "toolName,asc"),
                    @Parameter(
                            name = "filter",
                            in = ParameterIn.QUERY,
                            description = "Filter criteria in the format `field:value1,value2`. " +
                                    "Can be repeated for multiple filters. Examples: " +
                                    "`toolName:Drill`, `brand:Bosch`, `category.name:Hand Tools,Power Tools`.",
                            examples = {
                                    @ExampleObject(name = "Single value", value = "toolName:Drill"),
                                    @ExampleObject(name = "Multiple values", value = "category.name:Hand Tools,Power Tools"),
                                    @ExampleObject(name = "Combined filters", value = "toolName:Drill&filter=brand:Bosch")
                            }
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tools retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Page<ToolResponse>> getPage(
            @PageableDefault(sort = "toolName", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(value = "filter", required = false) List<String> filters) {

        return ResponseEntity.ok(toolService.getPage(pageable, filters));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'TOOL_ADMINISTRATOR')")
    @Operation(
            summary = "Get Tool by ID",
            description = "Fetches a tool using its unique identifier.",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Tool ID", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tool retrieved successfully", content = @Content(schema = @Schema(implementation = ToolResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tool not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<ToolResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(toolService.getOne(id));
    }

    @PutMapping("/stock/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'TOOL_ADMINISTRATOR')")
    @Operation(
            summary = "Update Tool Stock",
            description = "Updates only stock-related fields (quantity, available, damaged, onLoan) of a tool.",
            parameters = {
                    @Parameter(name = "id", description = "Tool ID", required = true, example = "1")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tool stock updated successfully"),
            @ApiResponse(responseCode = "404", description = "Tool not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ToolResponse> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody ToolStockRequest toolStockRequest) {

        ToolResponse updatedTool = toolService.updateStock(id, toolStockRequest);
        return ResponseEntity.ok(updatedTool);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'TOOL_ADMINISTRATOR', 'TEACHER')")
    @Operation(
            summary = "Get all tools",
            description = "Retrieves a complete list of tools available in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of tools retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ToolResponse.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied - requires ADMINISTRATOR, TOOL_ADMINISTRATOR, or TEACHER role",
                            content = @Content(schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Unexpected server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    public ResponseEntity<List<ToolResponse>> getAllTools() {
        List<ToolResponse> tools = toolService.getAll();
        return ResponseEntity.ok(tools);
    }
}