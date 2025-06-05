package com.codeflow.toolflow.controller.headquarter;

import com.codeflow.toolflow.dto.ApiError;
import com.codeflow.toolflow.dto.headquarter.HeadquarterRequest;
import com.codeflow.toolflow.dto.headquarter.HeadquarterResponse;
import com.codeflow.toolflow.service.headquarter.HeadquarterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

@RestController
@RequestMapping("/headquarters")
public class HeadquarterController {

    @Autowired
    private HeadquarterService headquarterService;

    @PostMapping()
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "Register a New Headquarter",
            description = "Creates a new headquarter with name, address, and responsible user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Headquarter creation payload.",
                    content = @Content(
                            schema = @Schema(implementation = HeadquarterRequest.class),
                            examples = @ExampleObject(name = "Sample Headquarter", value = """
                                    {
                                      "name": "Central Warehouse",
                                      "address": "123 Main St",
                                      "responsibleId": 3
                                    }
                                    """)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Headquarter created", content = @Content(schema = @Schema(implementation = HeadquarterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<HeadquarterResponse> registerOne(@Valid @RequestBody HeadquarterRequest request) {
        HeadquarterResponse headquarter = headquarterService.registerOne(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(headquarter);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "Update Headquarter",
            description = "Updates the name, address, or responsible of an existing headquarter.",
            parameters = @Parameter(
                    in = ParameterIn.PATH,
                    name = "id",
                    description = "Headquarter ID",
                    required = true,
                    example = "1"
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = HeadquarterRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Headquarter updated", content = @Content(schema = @Schema(implementation = HeadquarterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Headquarter not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<HeadquarterResponse> updateOne(@PathVariable Long id, @Valid @RequestBody HeadquarterRequest request) {
        HeadquarterResponse headquarter = headquarterService.updateOne(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(headquarter);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "Delete Headquarter",
            description = "Performs a logical delete of the headquarter (sets status to false). Main headquarters cannot be deleted.",
            parameters = @Parameter(
                    in = ParameterIn.PATH,
                    name = "id",
                    description = "ID of the headquarter to delete",
                    required = true,
                    example = "2"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Headquarter deleted"),
            @ApiResponse(responseCode = "400", description = "Cannot delete main or associated headquarter", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Headquarter not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> deleteOne(@PathVariable Long id) {
        headquarterService.deleteOne(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "Get Paginated Headquarters",
            description = "Returns a paginated list of all active headquarters.",
            parameters = {
                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number", example = "0"),
                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size", example = "10"),
                    @Parameter(name = "sort", in = ParameterIn.QUERY, description = "Sort field and direction", example = "name,asc")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Page<HeadquarterResponse>> getPage(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<HeadquarterResponse> headquarters = headquarterService.getPage(pageable);
        return ResponseEntity.ok(headquarters);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "Get Headquarter by ID",
            description = "Returns the details of a specific headquarter.",
            parameters = @Parameter(
                    in = ParameterIn.PATH,
                    name = "id",
                    description = "ID of the headquarter",
                    required = true,
                    example = "5"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Headquarter retrieved", content = @Content(schema = @Schema(implementation = HeadquarterResponse.class))),
            @ApiResponse(responseCode = "404", description = "Headquarter not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<HeadquarterResponse> getOne(@PathVariable Long id) {
        HeadquarterResponse headquarter = headquarterService.getOne(id);
        return ResponseEntity.ok(headquarter);
    }

    @GetMapping("/all")
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Get All Headquarters",
            description = "Returns all active headquarters (status = true). No auth required."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of headquarters", content = @Content(schema = @Schema(implementation = HeadquarterResponse.class)))
    })
    public ResponseEntity<List<HeadquarterResponse>> getAll() {
        List<HeadquarterResponse> headquarters = headquarterService.getAll();
        return ResponseEntity.ok(headquarters);
    }
}
