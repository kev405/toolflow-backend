package com.codeflow.toolflow.controller.loan;

import com.codeflow.toolflow.dto.ApiError;
import com.codeflow.toolflow.dto.loan.LoanRequest;
import com.codeflow.toolflow.dto.loan.LoanResponse;
import com.codeflow.toolflow.service.loan.LoanService;
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

/**
 * REST controller for managing loan operations.
 */
@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'TOOL_ADMINISTRATOR', 'TEACHER')")
    @Operation(
            summary = "Register a New Loan",
            description = "Creates a new loan using the provided loan details including teacher, responsible user, due date, notes, and selected tools.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Loan creation payload. Includes teacher ID, responsible user ID, due date, notes, and tool details.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoanRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Sample Loan",
                                            value = """
                                                    {
                                                      "teacherId": 1,
                                                      "responsibleId": 2,
                                                      "dueDate": "2025-05-21",
                                                      "notes": "Loan for project X",
                                                      "tools": [
                                                        {
                                                          "id": 101,
                                                          "requested": 2,
                                                          "notes": "For main prototype",
                                                          "responsibleId": 2
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Loan successfully created",
                    content = @Content(schema = @Schema(implementation = LoanResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or validation error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<LoanResponse> registerOne(@Valid @RequestBody LoanRequest loanRequest) {
        LoanResponse response = loanService.registerOne(loanRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'TOOL_ADMINISTRATOR', 'TEACHER')")
    @Operation(
            summary = "Get Paginated Loans",
            description = """
                    Retrieves a paginated list of loans. Supports filtering by teacher ID, responsible ID, due date, and loan status.
                    Example filters:
                    - `teacherId:1`
                    - `responsibleId:5`
                    - `dueDate:2025-05-20`
                    - `loanStatus:ON_LOAN`
                    
                    Multiple filters can be sent as repeated `filter` parameters in the query string.
                    """,
            parameters = {
                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number", example = "0"),
                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size", example = "10"),
                    @Parameter(name = "sort", in = ParameterIn.QUERY, description = "Sort format: property,asc|desc", example = "dueDate,desc"),
                    @Parameter(
                            name = "filter",
                            in = ParameterIn.QUERY,
                            description = "Filter criteria in `key:value` format. Can be repeated for multiple filters.",
                            examples = {
                                    @ExampleObject(name = "Teacher ID", value = "teacherId:1"),
                                    @ExampleObject(name = "Loan Status", value = "loanStatus:ON_LOAN"),
                                    @ExampleObject(name = "Due Date", value = "dueDate:2025-05-20")
                            }
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Loans retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<Page<LoanResponse>> getPage(@PageableDefault(sort = "dueDate", direction = Sort.Direction.DESC) Pageable pageable,
                                                      @RequestParam(value = "filter", required = false) List<String> filters) {
        Page<LoanResponse> loans = loanService.getAll(pageable, filters);
        return ResponseEntity.ok(loans);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'TOOL_ADMINISTRATOR', 'TEACHER')")
    @Operation(
            summary = "Update Loan",
            description = """
                    Updates the details of an existing loan. The allowed modifications depend on the user's role:
                    - TEACHER can update the responsible user, notes, and tools (requested quantities).
                    - ADMINISTRATOR / TOOL_ADMINISTRATOR can also modify loaned, delivered, and damaged quantities.
                    
                    Additionally, the loan status may transition automatically based on the updated values.
                    For example:
                    - From ORDER to ON_LOAN if all 'loaned' values are assigned.
                    - To RETURNED, MISSING_RETURNED, or DAMAGED_RETURNED based on delivered and damaged quantities.
                    """,
            parameters = {
                    @Parameter(
                            in = ParameterIn.PATH,
                            name = "id",
                            description = "ID of the loan to be updated",
                            required = true,
                            example = "101"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Loan update payload with information about the responsible person, notes, due date, and tools.",
                    content = @Content(schema = @Schema(implementation = LoanRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Loan successfully updated",
                    content = @Content(schema = @Schema(implementation = LoanResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or update not permitted",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Loan or responsible user not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<LoanResponse> updateOne(@Valid @RequestBody LoanRequest loanRequest,
                                                  @PathVariable Long id) {
        LoanResponse response = loanService.updateOne(loanRequest, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'TOOL_ADMINISTRATOR', 'TEACHER')")
    @Operation(
            summary = "Delete Loan",
            description = "Performs a soft delete on a loan by setting its status to false. This operation is only allowed if the loan is in the 'ORDER' state.",
            parameters = {
                    @Parameter(
                            in = ParameterIn.PATH,
                            name = "id",
                            description = "ID of the loan to be deleted",
                            required = true,
                            example = "42"
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Loan successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Loan cannot be deleted due to invalid state (e.g., not in ORDER)",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Loan not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<Void> deleteOne(@PathVariable Long id) {
        loanService.deleteOne(id);
        return ResponseEntity.noContent().build();
    }
}

