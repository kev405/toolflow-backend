package com.codeflow.toolflow.controller.user;

import com.codeflow.toolflow.dto.ApiError;
import com.codeflow.toolflow.dto.user.UserRequest;
import com.codeflow.toolflow.dto.user.UserResponse;
import com.codeflow.toolflow.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
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

/**
 * REST controller for managing user operations.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Register New User",
            description = "Registers a new user in the system using the provided user details. Returns the details of the newly created user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration payload. Contains information such as name, username, password, repeatedPassword, lastName, phone, email, status, createdBy, updatedBy, and role.",
                    content = @Content(
                            schema = @Schema(implementation = UserRequest.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered", content = @Content(schema = @Schema(implementation = UserResponse.class)))
            , @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ApiError.class)))
            , @ApiResponse(responseCode = "409", description = "Username already exists", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping()
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<UserResponse> registerOne(@RequestBody @Valid UserRequest userRequest) {
        UserResponse user = userService.registerOneUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(
            summary = "Update User",
            description = "Updates an existing user in the system using the provided user details. Returns the updated user's details.",
            parameters = {
                    @Parameter(
                            in = ParameterIn.PATH,
                            name = "id",
                            description = "ID of the user to be updated",
                            required = true
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User update payload. Contains information such as name, username, password, repeatedPassword, lastName, phone, email, status, createdBy, updatedBy, and roles.",
                    content = @Content(
                            schema = @Schema(implementation = UserRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User updated successfully",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<UserResponse> updateOne(@PathVariable Long id, @RequestBody @Valid UserRequest userRequest) {
        UserResponse user = userService.updateOneUser(id, userRequest);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @Operation(
            summary = "Delete User",
            description = "Deletes an existing user from the system using the provided user ID.",
            parameters = {
                    @Parameter(
                            in = ParameterIn.PATH,
                            name = "id",
                            description = "ID of the user to be deleted",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteOne(@PathVariable Long id) {
        userService.deleteOneUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get paginated list of active users",
            description = "Retrieves a paginated list of active users. You can optionally filter the results by providing a search term and a column to search. " +
                    "Supported search columns: id, username, name, lastName, email.",
            parameters = {
                    @Parameter(
                            name = "page",
                            in = ParameterIn.QUERY,
                            description = "Page number (0-based index)",
                            example = "0"
                    ),
                    @Parameter(
                            name = "size",
                            in = ParameterIn.QUERY,
                            description = "Number of records per page",
                            example = "10"
                    ),
                    @Parameter(
                            name = "sort",
                            in = ParameterIn.QUERY,
                            description = "Sorting criteria in the format: property(,asc|desc). Default is name,asc",
                            example = "name,asc"
                    ),
                    @Parameter(
                            name = "search",
                            in = ParameterIn.QUERY,
                            description = "Search term to filter the users",
                            required = false
                    ),
                    @Parameter(
                            name = "searchColumn",
                            in = ParameterIn.QUERY,
                            description = "Column on which to perform the search. Allowed values: id, username, name, lastName, email",
                            required = false
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Users retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid search parameters supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @GetMapping()
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Page<UserResponse>> getPage(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "searchColumn", required = false) String searchColumn) {

        Page<UserResponse> users = userService.getPage(pageable, search, searchColumn);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Get a User by ID",
            description = "Retrieves a single user by its unique identifier.",
            parameters = {
                    @Parameter(
                            name = "id",
                            in = ParameterIn.PATH,
                            description = "ID of the user to retrieve",
                            required = true,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<UserResponse> getOne(@PathVariable Long id) {
        UserResponse user = userService.getOne(id);
        return ResponseEntity.ok(user);
    }
}
